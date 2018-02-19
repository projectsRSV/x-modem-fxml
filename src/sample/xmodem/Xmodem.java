package sample.xmodem;

import com.fazecast.jSerialComm.SerialPort;
import com.google.common.primitives.Bytes;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.PortUnreachableException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by RSV on 30.06.2017.
 */
public class Xmodem {

    public final static boolean DEBUG = false;
//    public final static boolean DEBUG = true;
//    private final static String deviceName = ".*AT Port.*";

    private final static byte SOH = 1;
    private final static byte EOT = 4;
    private final static byte ACK = 6;
    private final static byte NAK = 0x15;
    private final static byte SUB = 26;

    //    private static InputStream inputStream;
//    private static OutputStream outputStream;
    private static SerialPort serialPort;

    public static SerialPort getSerialPort() {
        return serialPort;
    }

    public static void setSerialPort(SerialPort serialPort) {
        Xmodem.serialPort = serialPort;
    }

    public static void writeData(byte data) throws PortUnreachableException {
        if (DEBUG) {
            System.out.print("\nsend: ");
            System.out.println(Integer.toHexString((int) data));
        }
        try {
            getOutputStream().write(data);
        } catch (Exception e) {
            throw new PortUnreachableException();
        }
    }

    public static void writeData(byte[] bufFrom) throws PortUnreachableException {
        if (DEBUG) {
            System.out.print("\nsending: ");
            for (int i = 0; i < bufFrom.length; i++) {
                System.out.print((char) bufFrom[i]);
            }
        }
        for (byte b : bufFrom) {
            try {
                getOutputStream().write(b);
            } catch (Exception e) {
                throw new PortUnreachableException();
            }
        }
    }

    public static boolean readFileXmodem(ArrayList<Byte> list) throws PortUnreachableException {
        int count1 = 0;
        int count2 = 0;
        int header = 0;
        byte[] buff = new byte[128];

        try {
            while (true) {
                count1 = getInputStream().available();
                if (count1 > 0) {
                    if ((header = getInputStream().read()) == EOT) {
                        if (DEBUG) System.out.println("\nEND of TRANSMISSION");
                        return true;
                    } else if (header == 0) {
                    } else if (header == SOH) {
                        count1 = getInputStream().read();                //packet count
                        count2 = getInputStream().read();                //check packet count
                        if (count1 + (count2) != 0xff) {
                            if (DEBUG)
                                System.out.println("error numbers of packets");
                            return false;
                        }
                        byte byteRec = 0;
                        for (int i = 0; i < 128; i++) {             //main data
                            byteRec = (byte) getInputStream().read();
                            buff[i] = byteRec;
//                            if (byteRec != SUB) {                  //if that .zip error, .txt norm
                            list.add(byteRec);
//                            }
                        }
                        count1 = getInputStream().read();                //crc first byte
                        count2 = getInputStream().read();                //crc second byte
                        int receivedCheckSum = ((count1 << 8) | count2);
                        int calculatedCheckSum = sample.xmodem.CRC16CCITT.calcOrigin(buff);
                        if (receivedCheckSum != calculatedCheckSum) {
                            if (DEBUG)
                                System.out.println("error check sum");
                            return false;
                        }
                        if (DEBUG) {
                            for (byte b : buff) {
                                System.out.print((char) b);
                            }
                        }
//                        System.out.println(new String(buff, 0, buff.length));
                    } else if (DEBUG) System.out.print("error of EOT: " + (char) (header));
//                    Main.pause(1);
                    writeData(ACK);    //send ack
                    writeData(ACK);    //send ack
                    writeData(ACK);    //send ack
                    pause(20);
                } else {
//                    Main.pause(1);
                    writeData(ACK);    //send ack
                    writeData(ACK);    //send ack
                    writeData(ACK);    //send ack
                    return false;
                }
            }
        } catch (Exception e) {
            throw new PortUnreachableException();
        }
//        return false;
    }

    /*public static boolean checkReadWrite() throws PortUnreachableException {
        sendCommandUart("AT");
        pause(100);
        try {
            if (readDataUART().length >= 2) return true;
            else return false;
        } catch (NullPointerException e) {
            return false;
        }
    }*/

    public static void sendCommandUart(String command) throws PortUnreachableException {
        byte[] buffer = new byte[command.getBytes().length + 2];
        System.arraycopy(command.getBytes(), 0, buffer, 0, command.length());
        buffer[command.getBytes().length + 0] = 13;
        buffer[command.getBytes().length + 1] = 10;
        writeData(buffer);
    }

    private static void sendReadyXmodem() throws PortUnreachableException {
        byte readyCommand = 0x43;
        writeData(readyCommand);
    }

    public static void receiveFile(File file, String command) throws PortUnreachableException {
        int endCount = 0;
        boolean complete = false;
        ArrayList<Byte> list = null;

        while (complete != true) {
            if (endCount++ >= 3) break;
            list = new ArrayList<>();
            sendCommandUart(command);
            pause(1000);
            readDataUART();
            pause(1000);
            sendReadyXmodem();
            pause(300);
            complete = readFileXmodem(list);
        }

        if (Xmodem.DEBUG) {
            for (Byte b : list) {
                System.out.print((char) (byte) b);
            }
            try {
                System.out.println("\ncomplete " + getInputStream().available());
            } catch (IOException e) {
                throw new PortUnreachableException();
            }
        }
        writeToFile(Bytes.toArray(list), file);
    }

    private static void writeToFile(byte[] bytes, File file) {
        Path path = Paths.get(file.getAbsolutePath());
        try {
            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void pause(int pause) {
        try {
            TimeUnit.MILLISECONDS.sleep(pause);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static byte[] readDataUART() throws PortUnreachableException {
        int numBytes;
        byte[] buffTo = null;
        while (true) {
            try {
                if ((numBytes = getInputStream().available()) > 0) {
                    buffTo = new byte[numBytes];
                    getInputStream().read(buffTo);
                    if (DEBUG) System.out.println(new String(buffTo, 0, buffTo.length));
                } else {
                    break;
                }
            } catch (Exception e) {
                throw new PortUnreachableException();
            }
        }
        return buffTo;
    }

    public static byte[] read(int typeOfBlocking, int timeoutRead, int sizeBuffer) {
        serialPort.setComPortTimeouts(typeOfBlocking, timeoutRead, 0);
        byte[] readBuffer = new byte[sizeBuffer];
        try {
            int numRead = serialPort.readBytes(readBuffer, readBuffer.length);
            if (Xmodem.DEBUG) System.out.println("Read " + numRead + " bytes. ");
        } catch (Exception e) { e.printStackTrace(); }
        return readBuffer;
    }
//    public static void setInputStream(InputStream inputStream) {
//        Xmodem.inputStream = inputStream;
//    }

//    public static void setOutputStream(OutputStream outputStream) {
//        Xmodem.outputStream = outputStream;
//    }

    public static OutputStream getOutputStream() {
        return serialPort.getOutputStream();
    }

    public static InputStream getInputStream() {
        return serialPort.getInputStream();
    }
}


