package sample.xmodem;

import com.fazecast.jSerialComm.SerialPort;

import java.io.File;
import java.io.IOException;
import java.net.PortUnreachableException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by RSV on 30.06.2017.
 */
public class Xmodem {

    //    public final static boolean DEBUG = false;
    public final static boolean DEBUG = true;
//    private final static String deviceName = ".*AT Port.*";

    private final static byte SOH = 1;
    private final static byte EOT = 4;
    private final static byte ACK = 6;
    private final static byte NACK = 0x15;
    private final static byte SUB = 26;
    private final static byte READY = 0x43;

    private static SerialPort serialPort;

    public static void setSerialPort(SerialPort serialPort) {
        Xmodem.serialPort = serialPort;
    }

    public static void sendCommand(String command) throws PortUnreachableException {
        command += "\r\n";
        byte[] buffer = command.getBytes();
        writeData(buffer);
    }

    public static void receiveFile(File file, String command) throws PortUnreachableException {
        XmodemBlock xmodemBlock;
        List<Byte> byteList = new ArrayList<>();

        sendCommand(command);
        Xmodem.read(SerialPort.TIMEOUT_READ_BLOCKING, 2000, 200);
        if (DEBUG) System.out.println("sending READY");
        writeData(READY);
        while (true) {
            xmodemBlock = new XmodemBlock(Xmodem.read(SerialPort.TIMEOUT_READ_BLOCKING, 2000, 134));
            xmodemBlock.setList(byteList);
            if (xmodemBlock.getHeader() == EOT) break;
            /*try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            if (xmodemBlock.getBlockNumber() == -1) {
                if (DEBUG) System.out.println("\nblock number incorrect");
                if (DEBUG) System.out.println("\nsending NACK");
                writeData(NACK);
            } else if (!xmodemBlock.isCRCCorrect()) {
                if (DEBUG) System.out.println("\ncrc incorrect");
                if (DEBUG) System.out.println("\nsending NACK");
                writeData(NACK);
            } else {
                if (DEBUG) System.out.println("\nsending ACK");
                writeData(ACK);
            }
        }

        byte[] fileAsByte = new byte[byteList.size()];
        for (int i = 0; i < byteList.size(); i++) {
            fileAsByte[i] = byteList.get(i);
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
        if (DEBUG) {
            System.out.print("\nsending: ");
            for (int i = 0; i < bufFrom.length; i++) {
                System.out.print((char) bufFrom[i]);
            }
        }
        serialPort.writeBytes(bufFrom, bufFrom.length);
    }
}


