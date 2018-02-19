package sample.xmodem.commands;


import java.util.ArrayList;
import java.util.List;

public class CommandUarfcn implements MakeCommandInt {
    private final int index = id++;
    private static int id = 1;
    private List<String> uarfcnList = new ArrayList<>();
    private boolean setUarfcn;

    public CommandUarfcn() {
    }

    public CommandUarfcn(List<String> uarfcnList) {
        this.uarfcnList = uarfcnList;
    }

    @Override
    public String getCommand() {
        String request = Commands.SET_FREQ.getCommand() + index + ",";
        for (String s : uarfcnList) {
            request = request + s + ",";
        }
        return request.substring(0, request.length() - 1);
    }

    @Override
    public boolean isValid() {
        return (uarfcnList.isEmpty()) ? false : true;
    }

    public List<String> getUarfcnList() {
        return uarfcnList;
    }

    public void setUarfcnList(List<String> uarfcnList) {
        this.uarfcnList = uarfcnList;
    }

    public boolean isSetUarfcn() {
        return setUarfcn;
    }

    public void setSetUarfcn(boolean setUarfcn) {
        this.setUarfcn = setUarfcn;
    }

    @Override
    public String toString() {
        return "CommandUarfcn{" +
                "index=" + index +
                ", uarfcnList=" + uarfcnList +
                ", setUarfcn=" + setUarfcn +
                '}';
    }
}
