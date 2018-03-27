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


public class Xmodem {

        public final static boolean DEBUG = false;
//    public final static boolean DEBUG = true;

    final static byte SOH = 1;
    final static byte EOT = 4;
    final static byte ETB = 0x17;
    final static byte CAN = 0x18;
    final static byte ACK = 6;
    final static byte NACK = 0x15;
    final static byte SUB = 26;
    final static byte[] READY = {0x43, 0x00, 0x2e};     //C
    //    final static byte READY = 0x43;     //C
    final static int NUM_BYTES = 134;

    private static SerialPort serialPort;
    private static int countEmptyBlock;
    private static int countBreaks;
    private static Thread pauserThread;
    volatile private static boolean timePauseElapsed;

    public static void setSerialPort(SerialPort serialPort) {
        Xmodem.serialPort = serialPort;
    }

    public static void sendCommand(String command) throws PortUnreachableException {
        command += "\r\n";
        if (DEBUG) System.out.print("\n" + command);
        byte[] buffer = command.getBytes();
        writeData(buffer);
    }

    private static byte[] readFileXmodem() throws PortUnreachableException {
        byte[] buff = new byte[NUM_BYTES];
        try {
            for (int i = 0; i < NUM_BYTES; i++) {
                if (serialPort.bytesAvailable() > 0) {
                    buff[i] = (byte) serialPort.getInputStream().read();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
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
            if (countBreaks >= 5) {
                if (DEBUG) System.out.print("\nthrow NullPointerException");
                countBreaks = 0;
                countEmptyBlock = 0;
                throw new NullPointerException();
            }
            xmodemBlock = new XmodemBlock();
            byteList = new ArrayList<>();
            beginTransaction(command, buffSize);

            while (true) {
                xmodemBlock.setBlockData(readFileXmodem());
                if (xmodemBlock.isEndOfTransmission()) {
                    countBreaks = 0;
                    countEmptyBlock = 0;
                    pause(20);
                    writeData(ACK);
                    if (DEBUG) System.out.print("\nEND of TRANSMISSION");
                    break;
                } else if (xmodemBlock.isStartOfTransmission()) {
                    if (xmodemBlock.isBlockNumCorrect() && xmodemBlock.isCRCCorrect()) {
//                        if (DEBUG) System.out.print("\n" + xmodemBlock.toString());
                        int countAck = 0;
                        do {
                            writeData(ACK);
                            pause(2);
                            if (countAck++ >= 25) {
                                countBreaks++;
                                break;
                            }
                        } while (serialPort.bytesAvailable() == 0);
                    } else {
                        if (DEBUG) System.out.print("\nerror block number or crc");
                        writeData(NACK);
                    }
                } else if (xmodemBlock.isEmpty()) {
                    if (DEBUG) System.out.print("\nblock is empty");
                    if (countEmptyBlock++ >= 5) {
                        if (DEBUG) System.out.print("\nthrow RebootDeviceException");
                        countEmptyBlock = 0;
                        countBreaks = 0;
                        throw new RebootDeviceException();
                    }
                    writeData(NACK);
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
            if (!timePauseElapsed) {
                if (count++ >= 2) throw new RebootDeviceException();
                timePauseElapsed = true;
                sendCommand(command);
                read(SerialPort.TIMEOUT_READ_BLOCKING, 2000, buffSize);
                writeData(READY);
                setPauseThread(5);
            }
            if (serialPort.bytesAvailable() > 0) {
                System.out.print("\nbytes availible after ready = " + serialPort.bytesAvailable());
                pauserThread.interrupt();
                break;
            }
        }
    }

    private static void setPauseThread(int seconds) {
        pauserThread = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(seconds);
            } catch (InterruptedException e) {
            } finally {
                timePauseElapsed = false;
            }
        });
        pauserThread.start();
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
        /*if (DEBUG) {
            System.out.print("\nsending: ");
            for (int i = 0; i < bufFrom.length; i++) {
                System.out.print((char) bufFrom[i]);
            }
        }*/
    }

    private static void pause(int pause) {
        try {
            TimeUnit.MILLISECONDS.sleep(pause);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


