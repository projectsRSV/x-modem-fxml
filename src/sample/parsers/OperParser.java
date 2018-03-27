package sample.parsers;


import javafx.scene.control.SpinnerValueFactory;
import sample.main.OperGUIList;
import sample.xmodem.Xmodem;

import java.util.ArrayList;
import java.util.List;

public class OperParser {
    private OperGUIList guiLists;

    public OperParser(OperGUIList guiLists) {
        this.guiLists = guiLists;
    }

    public void parseData(byte[] bytes) {
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
        for (String s : list) {
            if (Xmodem.DEBUG) System.out.println(s);
        }
        List<String> longNameList = new ArrayList<>();
        List<String> shortNameList = new ArrayList<>();
        List<String> mccList = new ArrayList<>();
        List<String> mncList = new ArrayList<>();
        List<String> gsmList = new ArrayList<>();
        List<String> wcdmaList = new ArrayList<>();

        for (String s : list) {
//            String[] lines = s.split("[.,\\u0020]");
            String[] lines = s.split("[\\s+]");
            List<String> stringList = new ArrayList<>();
            for (String line : lines) {
                if (!"".equals(line))
                    stringList.add(line);
            }
            if (stringList.size() == 6) {
                longNameList.add(stringList.get(1));
                shortNameList.add(stringList.get(2));
                mccList.add(stringList.get(3).substring(0, 3));
                mncList.add(stringList.get(3).substring(3, 5));
                gsmList.add(stringList.get(4));
                wcdmaList.add(stringList.get(5));
            }
        }
        try {
            for (int i = 0; i < longNameList.size(); i++) {
                try {
//                    guiLists.getLongNameFieldList().get(i).setText("");
                    guiLists.getLongNameFieldList().get(i).setText(longNameList.get(i));
                } catch (NullPointerException e) {
                    System.out.print("\n******************************************");
                    System.out.print("\nlongNameList.size = " + longNameList.size());
                    System.out.print("\nLongNmaeFieldList = " + guiLists.getLongNameFieldList());
                    System.out.print("\n******************************************");
                    throw new NullPointerException();
                }
            }
            for (int i = 0; i < shortNameList.size(); i++) {
                shortNameList.get(i);
                guiLists.getShortNameFieldList().get(i);
//                guiLists.getShortNameFieldList().get(i).setText("");
                guiLists.getShortNameFieldList().get(i).setText(shortNameList.get(i));
            }
            for (int i = 0; i < mccList.size(); i++) {
                try {
//                    guiLists.getMccNameFieldList().get(i).setText("");
                    guiLists.getMccNameFieldList().get(i).setText(mccList.get(i));
                } catch (NullPointerException e) {
                    System.out.print("\n******************************************");
                    System.out.print("\nMccNameFieldList.size = " + mccList.size());
                    System.out.print("\nmccList = " + guiLists.getMccNameFieldList());
                    System.out.print("\n******************************************");
                    throw new NullPointerException();
                }
            }
            for (int i = 0; i < mncList.size(); i++) {
//                guiLists.getSpinnerList().get(i).getValueFactory().setValue(Integer.valueOf(mncList.get(i)));
                SpinnerValueFactory<Integer> valueFactory =
                        new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99,
                                Integer.valueOf(mncList.get(i)));
                guiLists.getSpinnerList().get(i).setValueFactory(valueFactory);
            }
            for (int i = 0; i < gsmList.size(); i++) {
                guiLists.getGsmCheckBoxList().get(i).setSelected((gsmList.get(i).equals("1")) ? true : false);
            }
            for (int i = 0; i < wcdmaList.size(); i++) {
                guiLists.getWcdmaCheckBoxList().get(i).setSelected((wcdmaList.get(i).equals("1")) ? true : false);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

}
