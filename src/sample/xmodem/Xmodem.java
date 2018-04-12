package sample.xmodem;

import com.fazecast.jSerialComm.SerialPort;
import sample.exceptions.RebootDeviceException;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.PortUnreachableException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Xmodem {
    private final static Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    //        public final static boolean DEBUG = false;
//    public final static boolean DEBUG = true;

    final static byte SOH = 1;
    final static byte EOT = 4;
    final static byte ETB = 0x17;
    final static byte CAN = 0x18;
    final static byte ACK = 6;
    final static byte NACK = 0x15;
    final static byte SUB = 26;
    //    final static byte[] READY = {0x43, 0x00, 0x2e};     //C
    final static byte READY = 0x43;     //C
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

        LOGGER.info("starting Xmodem receiving");

        if (command.contains("ALL")) buffSize = 146;
        else buffSize = 111;

        do {
            if (countBreaks >= 5) {
                LOGGER.warning("throw NullPointerException");
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
                    LOGGER.info("END of TRANSMISSION");
                    break;
                } else if (xmodemBlock.isStartOfTransmission()) {
                    if (xmodemBlock.isBlockNumCorrect() && xmodemBlock.isCRCCorrect()) {
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
                        LOGGER.warning("error block number or crc");
                        writeData(NACK);
                    }
                } else if (xmodemBlock.isEmpty()) {
                    LOGGER.warning("block is empty");
                    if (countEmptyBlock++ >= 5) {
                        LOGGER.warning("throw RebootDeviceException");
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
                read(2000, buffSize);
                writeData(READY);
                setPauseThread(3);
            }
            if (serialPort.bytesAvailable() > 0) {
                LOGGER.info("bytes availible after ready = " + serialPort.bytesAvailable());
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

    public static byte[] read(int timeoutRead, int sizeBuffer) {
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, timeoutRead, 0);
        byte[] readBuffer = new byte[sizeBuffer];
        try {
            int numRead = serialPort.readBytes(readBuffer, readBuffer.length);
            LOGGER.info("Read " + numRead + " bytes: " + new String(readBuffer).replaceAll("\\s", ","));
        } catch (Exception e) { e.printStackTrace(); }
        return readBuffer;
    }

    private static void writeData(byte... bufFrom) throws PortUnreachableException {
        serialPort.writeBytes(bufFrom, bufFrom.length);
        if (bufFrom[0] == READY) LOGGER.info("send READY");
    }

    private static void pause(int pause) {
        try {
            TimeUnit.MILLISECONDS.sleep(pause);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


