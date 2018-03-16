package sample.xmodem;

public class CRC16CCITT {

 /*   public static int calc(byte[] bytes) {
//        int crc = 0xFFFF;
        int crc = 0;
        int polynomial = 0x1021;
        for (byte b : bytes) {
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b >> (7 - i) & 1) == 1);
                boolean c15 = ((crc >> 15 & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit) crc ^= polynomial;
            }
        }
        crc &= 0xffff;
        return crc;
    }*/

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

