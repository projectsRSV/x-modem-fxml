package sample.xmodem;


import java.util.Arrays;
import java.util.List;

public class XmodemBlock {
    /*size of block == 132 bytes*/
    private byte[] blockData;
    private byte[] mainData;
    private byte packetNum = 1;

    public void setBlockData(byte[] blockData) {
        this.blockData = blockData;
        calcMainData();
    }

    public boolean isEndOfTransmission() {
        return blockData[0] == Xmodem.EOT && blockData[1] == 0 && blockData[2] == 0;
    }


    public boolean isEmpty() {
        int sum = 0;
        for (byte b : blockData) sum |= b;
        return (sum == 0);
    }

    public boolean isStartOfTransmission() {
        return blockData[0] == Xmodem.SOH;
    }

    public boolean isBlockNumCorrect() {
        byte sum1 = blockData[1];
        byte sum2 = blockData[2];
        byte sum = (byte) (sum1 + sum2);
//        return (blockData[1] == packetNum++ && sum == 0xff) ? blockData[1] : -1;
//        return (blockData[1] == packetNum++ && sum == 0xff);
        return (sum == -1);
//        return true;
    }

    public boolean isCRCCorrect() {
        int crcLow = ((int) blockData[131] & 0xff) << 8;
        int crcHigh = (int) blockData[132] & 0xff;
        int calcCRC = CRC16CCITT.calcOrigin(mainData);
        return ((crcLow | crcHigh) == calcCRC);
    }

    public void saveToList(List<Byte> list) {
        for (byte b : this.mainData) {
            list.add(b);
        }
    }

    private byte[] calcMainData() {
        this.mainData = new byte[128];
        System.arraycopy(blockData, 3, this.mainData, 0, 128);
        return this.mainData;
    }

    @Override
    public String toString() {
        return "receive: " + Arrays.toString(blockData);
    }
}
