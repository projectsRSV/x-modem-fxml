package sample.parsers;


import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class OperSetParser {
    private final static Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    static public Set<String> parseData(byte[] bytes) {
        Set<String> acknowledgeList = new HashSet<>();
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
        LOGGER.info(list.toString());
        for (String s : list) {
            acknowledgeList.add(s);
        }
        return acknowledgeList;
    }
}
