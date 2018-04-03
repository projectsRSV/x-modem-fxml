package sample.parsers;


import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class OperSetParser {
    private final static Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

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
        LOGGER.info(list.toString());
        for (String s : list) {
            acknowledgeList.add(s);
        }
    }
}
