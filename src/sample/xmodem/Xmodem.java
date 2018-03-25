package sample.xmodem;

import com.fazecast.jSerialComm.SerialPort;
import sample.exceptions.RebootDeviceException;

import java.io.File;
import java.io.IOException;
import java.net.PortUnreachableException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by RSV on 30.06.2017.
 */
public class Xmodem {

    //    public final static boolean DEBUG = false;
    public final static boolean DEBUG = true;
//    private final static String deviceName = ".*AT Port.*";

    final static byte SOH = 1;
    final static byte EOT = 4;
    final static byte ETB = 0x17;
    final static byte CAN = 0x18;
    final static byte ACK = 6;
    final static byte NACK = 0x15;
    final static byte SUB = 26;
    final static byte[] READY = {0x43, 0x00, 0x2e};     //C

    private static SerialPort serialPort;
    private static int countReceive;
    private static Thread pauseThread;
    volatile private static boolean timePauseElapsed = true;

    public static void setSerialPort(SerialPort serialPort) {
        Xmodem.serialPort = serialPort;
    }

    public static void sendCommand(String command) throws PortUnreachableException {
        command += "\r\n";
        byte[] buffer = command.getBytes();
        writeData(buffer);
    }

    private static byte[] readFileXmodem() throws PortUnreachableException {
        byte[] buff = new byte[134];

        for (int i = 0; i < 134; i++) {
            try {
                if (serialPort.getInputStream().available() > 0)
                    buff[i] = (byte) serialPort.getInputStream().read();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return buff;
    }

    public static void receiveFile(File file, String command) throws PortUnreachableException {
        List<Byte> byteList;
        int buffSize;
        XmodemBlock xmodemBlock;

        if (command.contains("ALL")) buffSize = 146;
        else buffSize = 111;

        do {
            xmodemBlock = new XmodemBlock();
            byteList = new ArrayList<>();
            beginTransaction(command, buffSize);

            while (true) {
                xmodemBlock.setBlockData(readFileXmodem());
                if (DEBUG) System.out.print("\n" + xmodemBlock.toString());
                if (xmodemBlock.isEndOfTransmission()) {
                    pause(20);
                    writeData(ACK);
                    if (DEBUG) System.out.print("\nEND of TRANSMISSION");
                    break;
                } else if (xmodemBlock.isStartOfTransmission()) {
                    if (xmodemBlock.getBlockNumber() != -1 && xmodemBlock.isCRCCorrect()) {
                        int countAck = 0;
                        do {
                            writeData(ACK);
                            pause(2);
                            if (countAck++ >= 25) break;
                        } while (serialPort.bytesAvailable() == 0);
                        countReceive = 0;
                    } else {
                        if (DEBUG) System.out.print("\nerror block number or crc");
                        int countNack = 0;
                        do {
                            writeData(NACK);
                            pause(2);
                            if (countNack++ >= 25) break;
                        } while (serialPort.bytesAvailable() == 0);
                    }
                } else if (xmodemBlock.isEmpty()) {
                    if (DEBUG) System.out.print("\nblock is empty");
                    if (countReceive++ >= 5) {
                        if (DEBUG) System.out.print("\nthrow RebootDeviceException");
                        countReceive = 0;
                        throw new RebootDeviceException();
                    }
                    int countNack = 0;
                    do {
                        writeData(NACK);
                        pause(2);
                        if (countNack++ >= 25) break;
                    } while (serialPort.bytesAvailable() == 0);
                    break;
                }
                xmodemBlock.saveToList(byteList);
            }
        } while (!xmodemBlock.isEndOfTransmission());

        List<Byte> newByteList = new ArrayList<>();
        for (int i = 0; i < byteList.size(); i++) {
            if (!command.contains("ALL")) {
                if (byteList.get(i) != SUB) {
                    newByteList.add(byteList.get(i));
                }
            } else {
                newByteList.add(byteList.get(i));
            }
        }

        byte[] fileAsBytes = new byte[newByteList.size()];
        for (int i = 0; i < newByteList.size(); i++) {
            fileAsBytes[i] = newByteList.get(i);
        }

        writeToFile(fileAsBytes, file);
    }

    private static void beginTransaction(String command, int buffSize) throws PortUnreachableException {
        int count = 0;
        while (true) {
            if (timePauseElapsed) {
                if (count++ >= 1) throw new RebootDeviceException();
                timePauseElapsed = false;
                sendCommand(command);
                read(SerialPort.TIMEOUT_READ_BLOCKING, 2000, buffSize);
                writeData(READY);
                setPauseThread(3);
            }
            if (serialPort.bytesAvailable() > 0) {
                System.out.print("\nbytes availible after ready = " + serialPort.bytesAvailable());
                if (pauseThread.isAlive()) pauseThread.interrupt();
                break;
            }
        }
    }

    private static void setPauseThread(int seconds) {
        if (pauseThread != null && pauseThread.isAlive()) {
            pauseThread.interrupt();
            System.out.print("\nthread interrupted: " + pauseThread.isInterrupted() + " " + timePauseElapsed);
        }
        pauseThread = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(seconds);
            } catch (InterruptedException e) { }
            timePauseElapsed = true;
        });
        pauseThread.start();
    }

    private static void writeToFile(byte[] bytes, File file) {
        Path path = Paths.get(file.getAbsolutePath());
        try {
            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] read(int typeOfBlocking, int timeoutRead, int sizeBuffer) {
        serialPort.setComPortTimeouts(typeOfBlocking, timeoutRead, 0);
        byte[] readBuffer = new byte[sizeBuffer];
        try {
            int numRead = serialPort.readBytes(readBuffer, readBuffer.length);
            if (Xmodem.DEBUG) {
                System.out.println("\nRead " + numRead + " bytes: ");
                for (int i = 0; i < readBuffer.length; i++) {
                    System.out.print(readBuffer[i]);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return readBuffer;
    }

    private static void writeData(byte... bufFrom) throws PortUnreachableException {
        serialPort.writeBytes(bufFrom, bufFrom.length);
        if (DEBUG) {
            System.out.print("\nsending: ");
            for (int i = 0; i < bufFrom.length; i++) {
                System.out.print((char) bufFrom[i]);
            }
        }
    }

    private static void pause(int pause) {
        try {
            TimeUnit.MILLISECONDS.sleep(pause);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


