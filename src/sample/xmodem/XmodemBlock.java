package sample.xmodem;


import java.util.List;

public class XmodemBlock {
    /*size of block == 132 bytes*/
    private byte[] blockData;
    private byte[] mainData;

    public XmodemBlock(byte[] blockData) {
        this.blockData = blockData;
        calcMainData();
    }

    public int getHeader() {
        return blockData[0];
    }

    private byte[] calcMainData() {
        this.mainData = new byte[128];
        System.arraycopy(blockData, 3, this.mainData, 0, 128);
        return this.mainData;
    }

    public int getBlockNumber() {
        int sum = blockData[1] + blockData[2];
        return (sum == -1) ? blockData[1] : -1;
    }

    public boolean isCRCCorrect() {
        int crcLow = ((int) blockData[131] & 0xff) << 8;
        int crcHigh = (int) blockData[132] & 0xff;
        int calcCRC = CRC16CCITT.calcOrigin(mainData);
        return ((crcLow | crcHigh) == calcCRC);
    }

    public void setList(List<Byte> list) {
        for (byte b : this.mainData) {
            list.add(b);
        }
    }
}
