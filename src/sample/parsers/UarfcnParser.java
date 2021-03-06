package sample.parsers;


import sample.command.CommandUarfcn;
import sample.command.MakeCommandInt;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class UarfcnParser {
    private final static Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private List<MakeCommandInt> listUarfcn;

    public UarfcnParser(List<MakeCommandInt> listUarfcn) {
        this.listUarfcn = listUarfcn;
    }

    public void parseData(byte[] bytes) {
        List<Integer> list = new ArrayList<>();
        String message = "";
        for (byte b : bytes) {
            message += (char) b;
        }

        String[] strings = message.split("[\r,\n,.]");
        for (int i = 0; i < strings.length; i++) {
            try {
                int num = Integer.parseInt(strings[i]);
                list.add(num);
            } catch (NumberFormatException e) {
            }
        }
        if (!list.isEmpty()) {
            int numOperator = 0;
            CommandUarfcn commandUarfcn = null;
            for (MakeCommandInt c : listUarfcn) {
                ((CommandUarfcn) c).setUarfcnList(new ArrayList<>());
            }
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) < 5) {
                    numOperator = list.get(i);
                    commandUarfcn = ((CommandUarfcn) listUarfcn.get(numOperator - 1));
                } else {
                    commandUarfcn.getUarfcnList().add(String.valueOf(list.get(i)));
                }
            }
            LOGGER.info("parse UARFCN " + list.toString());
        }
    }
}
