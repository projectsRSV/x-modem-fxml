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
    private static boolean timePauseElapsed;
    private static Thread pauseThread;

    public static void setSerialPort(SerialPort serialPort) {
        Xmodem.serialPort = serialPort;
    }

    public static void sendCommand(String command) throws PortUnreachableException {
        command += "\r\n";
        byte[] buffer = command.getBytes();
        writeData(buffer);
    }

    private static void setPauseThread(int seconds) {
        pauseThread = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(seconds);
            } catch (InterruptedException e) {
            }
            timePauseElapsed = true;
        });
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
        XmodemBlock xmodemBlock = new XmodemBlock();
        List<Byte> byteList;
        int buffSize;
        int bytes;

        if (command.contains("ALL")) buffSize = 146;
        else buffSize = 111;
        do {
            byteList = new ArrayList<>();
            sendCommand(command);
            read(SerialPort.TIMEOUT_READ_BLOCKING, 2000, buffSize);

            timePauseElapsed = false;
            setPauseThread(10);
            pauseThread.start();
            writeData(READY);

            while (true) {
                if (timePauseElapsed) {
                    if (pauseThread.isAlive()) pauseThread.interrupt();
                    if (DEBUG) {
                        System.out.print("\nin ready block, when time is elapsed = " + serialPort.bytesAvailable());
                    }
                    setPauseThread(10);
                    pauseThread.start();
                    writeData(READY);
                    timePauseElapsed = false;
                }
                if ((bytes = serialPort.bytesAvailable()) > 0) {
                    System.out.print("\nbytes availible after ready = " + bytes);
                    break;
                }
            }
            if (pauseThread.isAlive()) pauseThread.interrupt();
            timePauseElapsed = false;

            while (true) {
                xmodemBlock.setBlockData(readFileXmodem());
                if (DEBUG) System.out.println("\n" + xmodemBlock.toString());
                if (xmodemBlock.isEndOfTransmission()) {
                    if (DEBUG) System.out.print("\nbefore send ACK EOT = " + serialPort.bytesAvailable());
                    pause(20);
//                    writeData(ACK);
//                    writeData(ACK);
                    writeData(ACK);
                    if (DEBUG) System.out.println("\nsending ACK when EOT");
                    break;
                } else if (xmodemBlock.getBlockNumber() != -1 && xmodemBlock.isCRCCorrect()) {
                    int countAck = 0;
                    if (DEBUG) System.out.print("\nbefore send ACK = " + serialPort.bytesAvailable());
//                    pause(20);
                    if (DEBUG) System.out.print("\nbefore send ACK pause= " + serialPort.bytesAvailable());
                    do {
                        writeData(ACK);    //send ack
                        pause(2);
                        if (countAck++ >= 25) break;
                    } while (serialPort.bytesAvailable() == 0);
                    if (DEBUG) System.out.println("\nsending ACK after receiving block");
                    countReceive = 0;
                } else if (xmodemBlock.isEmpty()) {
                    if (countReceive++ >= 3) {
                        if (DEBUG) System.out.println("\nthrow RebootDeviceException");
                        countReceive = 0;
                        throw new RebootDeviceException();
                    }
                    writeData(NACK);
                    break;
                } else if (xmodemBlock.getBlockNumber() == -1) {
                    if (DEBUG) System.out.println("\nblock number incorrect");
                    writeData(NACK);
                } else if (!xmodemBlock.isCRCCorrect()) {
                    if (DEBUG) System.out.println("\ncrc incorrect");
                    writeData(NACK);
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

        byte[] fileAsByte = new byte[newByteList.size()];
        for (int i = 0; i < newByteList.size(); i++) {
            fileAsByte[i] = newByteList.get(i);
        }

        writeToFile(fileAsByte, file);
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


