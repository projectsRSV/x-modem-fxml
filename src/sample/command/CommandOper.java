package sample.command;


public class CommandOper implements MakeCommandInt {
    private int index;
    private String longName;
    private String shortName;
    private String mcc;
    private byte mnc;
    private int gsm;
    private int wcdma;

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public String getLongName() {
        return longName;
    }

    public String getShortName() {
        return shortName;
    }

    public String getMcc() {
        return mcc;
    }

    public void setMcc(String mcc) {
        this.mcc = mcc;
    }

    public byte getMnc() {
        return mnc;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public void setMnc(byte mnc) {
        this.mnc = mnc;
    }

    public int getGsm() {
        return gsm;
    }

    public void setGsm(int gsm) {
        this.gsm = gsm;
    }

    public int getWcdma() {
        return wcdma;
    }

    public void setWcdma(int wcdma) {
        this.wcdma = wcdma;
    }

    @Override
    public String getCommand() {
        return Commands.SET_ALL.getCommand() + index + "," + longName + "," + shortName +
                "," + mcc + "," + ((mnc < 10) ? "0" : "") + mnc + "," + gsm + "," + wcdma;
    }

    @Override
    public boolean isValid() {
        return (longName.equals("") || shortName.equals("") || mcc.equals("") || mcc.length() != 3) ? false : true;
    }

    @Override
    public String toString() {
        return "CommandMain{" +
                "index=" + index +
                ", longName='" + longName + '\'' +
                ", shortName='" + shortName + '\'' +
                ", mcc='" + mcc + '\'' +
                ", mnc=" + mnc +
                ", gsm=" + gsm +
                ", wcdma=" + wcdma +
                '}';
    }
}
