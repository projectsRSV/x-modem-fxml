package sample.command;


import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ScanOperator {
    private final static Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    private int idOper;
    private int scanNum;
    private List<Integer> uarfcnList = new ArrayList<>();
    private List<Integer> lacList = new ArrayList<>();
    private List<Integer> cidList = new ArrayList<>();
    private List<Integer> rxList = new ArrayList<>();
    private BooleanProperty isCatcher = new SimpleBooleanProperty();
    private String startTime;
    private String endTime;

    public boolean isIsCatcher() {
        return isCatcher.get();
    }

    public ScanOperator() {
        isCatcher.set(false);
    }

    public BooleanProperty isCatcherProperty() {
        return isCatcher;
    }

    public void setIsCatcher(boolean isCatcher) {
        this.isCatcher.set(isCatcher);
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setIdOper(int idOper) {
        this.idOper = idOper;
    }

    public void setScanNum(int scanNum) {
        this.scanNum = scanNum;
    }

    public void setUarfcnList(List<Integer> uarfcnList) {
        this.uarfcnList = uarfcnList;
    }

    public void setLacList(List<Integer> lacList) {
        this.lacList = lacList;
    }

    public void setCidList(List<Integer> cidList) {
        this.cidList = cidList;
    }

    public void setRxList(List<Integer> rxList) {
        this.rxList = rxList;
    }

    public List<Integer> getUarfcnList() {
        return uarfcnList;
    }

    public List<Integer> getLacList() {
        return lacList;
    }

    public List<Integer> getCidList() {
        return cidList;
    }

    public List<Integer> getRxList() {
        return rxList;
    }

    public int getIdOper() {
        return idOper;
    }

    public int getScanNum() {
        return scanNum;
    }

    @Override
    public String toString() {
        return "ScanOperator{" +
                "idOper=" + idOper +
                ", scanNum=" + scanNum +
                ", uarfcnList=" + uarfcnList +
                ", lacList=" + lacList +
                ", cidList=" + cidList +
                ", rxList=" + rxList +
                ", isCatcher=" + isCatcher +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}
