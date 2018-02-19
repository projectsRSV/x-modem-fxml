package sample.main;


import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;

import java.util.List;

public class OperGuiList {
    private List<TextField> longNameFieldList;
    private List<TextField> shortNameFieldList;
    private List<TextField> mccNameFieldList;
    private List<Spinner<Integer>> spinnerList;
    private List<CheckBox> gsmCheckBoxList;
    private List<CheckBox> wcdmaCheckBoxList;

    public List<TextField> getLongNameFieldList() {
        return longNameFieldList;
    }

    public void setLongNameFieldList(List<TextField> longNameFieldList) {
        this.longNameFieldList = longNameFieldList;
    }

    public List<TextField> getShortNameFieldList() {
        return shortNameFieldList;
    }

    public void setShortNameFieldList(List<TextField> shortNameFieldList) {
        this.shortNameFieldList = shortNameFieldList;
    }

    public List<TextField> getMccNameFieldList() {
        return mccNameFieldList;
    }

    public void setMccNameFieldList(List<TextField> mccNameFieldList) {
        this.mccNameFieldList = mccNameFieldList;
    }

    public List<Spinner<Integer>> getSpinnerList() {
        return spinnerList;
    }

    public void setSpinnerList(List<Spinner<Integer>> spinnerList) {
        this.spinnerList = spinnerList;
    }

    public List<CheckBox> getGsmCheckBoxList() {
        return gsmCheckBoxList;
    }

    public void setGsmCheckBoxList(List<CheckBox> gsmCheckBoxList) {
        this.gsmCheckBoxList = gsmCheckBoxList;
    }

    public List<CheckBox> getWcdmaCheckBoxList() {
        return wcdmaCheckBoxList;
    }

    public void setWcdmaCheckBoxList(List<CheckBox> wcdmaCheckBoxList) {
        this.wcdmaCheckBoxList = wcdmaCheckBoxList;
    }
}
