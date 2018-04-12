package sample.parsers;


import sample.command.ScanOperator;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScanParser {
    private final static Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    //private final static String REG_EX = "([Operator#1])([\\s]+)([\\w]+)\2\3\2()\2";

    static public void parse(ScanOperator scanOperator, byte[] bytes) {
        LOGGER.info("start parsing data");
        ScanOperator newOperator;
        List<Integer> list = new ArrayList<>();
        String message = "";
        for (byte b : bytes) {
            message += (char) b;
        }
        String[] strings = message.split("[\r\n' ''#']");
        for (int i = 0; i < strings.length; i++) {
            try {
                int num = Integer.parseInt(strings[i]);
                list.add(num);
            } catch (NumberFormatException e) {
            }
        }
        try {
            if (list.size() >= 6) {
                newOperator = new ScanOperator();
                newOperator.setScanNum(list.get(0));
                newOperator.setIdOper(list.get(1));
                for (int i = 2; i < list.size(); i += 4) {
                    newOperator.getUarfcnList().add(list.get(i));
                    newOperator.getLacList().add(list.get(i + 1));
                    newOperator.getCidList().add(list.get(i + 2));
                    newOperator.getRxList().add(list.get(i + 3));
                }
                checkIfCatcher(scanOperator, newOperator);
            }
            LOGGER.info("operator info end of parsing: " + scanOperator.toString());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "exception while parsing", e);
        }
    }

    static private void checkIfCatcher(ScanOperator operator, ScanOperator newOperator) {
        long tempLastSum = 0;
        long tempPrevSum = 0;
        if (operator.getLacList().size() == newOperator.getLacList().size()) {
            for (int i = 0; i < newOperator.getLacList().size(); i++) {
                tempLastSum += newOperator.getLacList().get(i);
                tempPrevSum += operator.getLacList().get(i);
            }
        }
        operator.setLacList(newOperator.getLacList());
        operator.setCidList(newOperator.getCidList());
        operator.setUarfcnList(newOperator.getUarfcnList());
        operator.setRxList(newOperator.getRxList());
        operator.setIdOper(newOperator.getIdOper());
        operator.setScanNum(newOperator.getScanNum());
//        operator.setStartTime(newOperator.getStartTime());
//        operator.setEndTime(newOperator.getEndTime());
//        operator.setIsCatcher(newOperator.isIsCatcher());
        if (tempLastSum != tempPrevSum) {
            LOGGER.info(String.format("lastSum= %d prevSum= %d isCatcher= %b", tempLastSum, tempPrevSum, operator.isIsCatcher()));
            operator.setIsCatcher(!operator.isIsCatcher());
        }
    }
}
