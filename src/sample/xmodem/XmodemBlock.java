package sample.xmodem;


import java.util.Arrays;
import java.util.List;

public class XmodemBlock {
    /*size of block == 132 bytes*/
    private byte[] blockData;
    private byte[] mainData;

    public void setBlockData(byte[] blockData) {
        this.blockData = blockData;
        calcMainData();
    }

    public boolean isEndOfTransmission() {
        return blockData[0] == Xmodem.EOT && blockData[1] == 0 && blockData[2] == 0;
    }


    public boolean isEmpty() {
        int sum = 0;
        for (byte b : blockData) {
            sum |= b;
        }
        return (sum == 0);
    }

    public int getHeader() {
        return blockData[0];
    }
    public int getBlockNumber() {
        int sum = (blockData[1] + blockData[2] & 0xff);
        return (sum == 0xff) ? blockData[1] : -1;
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
        return Arrays.toString(blockData);
    }
}
