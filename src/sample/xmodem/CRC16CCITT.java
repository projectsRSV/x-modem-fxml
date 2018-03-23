package sample.xmodem;

public class CRC16CCITT {

     public static int calcOrigin(byte[] bytes) {
        int crc = 0;
        char i;
        for (int j = 0; j < bytes.length; j++) {
            crc = crc ^ (int) bytes[j] << 8;
            i = 8;
            do {
                if ((crc & 0x8000) > 0) crc = crc << 1 ^ 0x1021;
                else crc = crc << 1;
            } while (--i != 0);
        }
        return crc & 0xffff;
    }
}

