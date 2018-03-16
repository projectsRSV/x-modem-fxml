package sample.parsers;


import sample.xmodem.Xmodem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class OperSetParser {
    private Set<String> acknowledgeList;

    public OperSetParser(Set<String> acknowledgeList) {
        this.acknowledgeList = acknowledgeList;
    }

    public void parseData(byte[] bytes) {
        acknowledgeList.clear();
        List<String> list = new ArrayList<>();
        String message = "";
        for (byte b : bytes) {
            message += (char) b;
        }
        String[] strings = message.split("[\r,\n]");
        for (String s : strings) {
            if (!"".equals(s))
                list.add(s);
        }
        if (Xmodem.DEBUG) System.out.println(Arrays.asList(list));
        for (String s : list) {
            acknowledgeList.add(s);
        }
    }
}
