package sample.main;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import sample.command.ScanOperator;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class AlarmController {
    private final static Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private static int count = 1;
    private final int id = count++;

    @FXML
    private Label operatorNum;
    @FXML
    private Label uarfcnStartLabel1;
    @FXML
    private Label uarfcnStartLabel2;
    @FXML
    private Label uarfcnStartLabel3;
    @FXML
    private Label uarfcnEndLabel1;
    @FXML
    private Label uarfcnEndLabel2;
    @FXML
    private Label uarfcnEndLabel3;
    @FXML
    private Label lacStartLabel1;
    @FXML
    private Label lacStartLabel2;
    @FXML
    private Label lacStartLabel3;
    @FXML
    private Label lacEndLabel1;
    @FXML
    private Label lacEndLabel2;
    @FXML
    private Label lacEndLabel3;
    @FXML
    private Label rxStartLabel1;
    @FXML
    private Label rxStartLabel2;
    @FXML
    private Label rxStartLabel3;
    @FXML
    private Label rxEndLabel1;
    @FXML
    private Label rxEndLabel2;
    @FXML
    private Label rxEndLabel3;
    @FXML
    private Label timeStartLabel;
    @FXML
    private Label timeEndLabel;
    @FXML
    private Label uarfcnLabel;
    @FXML
    private Label lacLabel;
    @FXML
    private Label rxLevelLabel;
//    @FXML
//    private Label timeLabel;
    @FXML
    private Label operatorLabel;

//    private ScanOperator startOperator;
//    private ScanOperator endOperator;
    private List<Label> operatorLabelsListStart;
    private List<Label> operatorLabelsListEnd;

    @FXML
    private void initialize() {
        LOGGER.info("init alarmController");
        operatorLabelsListStart = Arrays.asList(
                uarfcnStartLabel1, uarfcnStartLabel2, uarfcnStartLabel3,
                lacStartLabel1, lacStartLabel2, lacStartLabel3,
                rxStartLabel1, rxStartLabel2, rxStartLabel3,
                timeStartLabel);
        operatorLabelsListEnd = Arrays.asList(
                uarfcnEndLabel1, uarfcnEndLabel2, uarfcnEndLabel3,
                lacEndLabel1, lacEndLabel2, lacEndLabel3,
                rxEndLabel1, rxEndLabel2, rxEndLabel3,
                timeEndLabel);
        for (Label l : operatorLabelsListEnd) {
            l.setText("");
        }

//        uarfcnLabel.getStyleClass().add("label-headers");
//        lacLabel.getStyleClass().add("label-headers");
//        rxLevelLabel.getStyleClass().add("label-headers");
//        timeEndLabel.getStyleClass().add("label-headers");
//        operatorLabel.getStyleClass().add("label-headers");
    }

    public void setStartOperator(ScanOperator startOperator) {
//        this.startOperator = startOperator;
        Platform.runLater(() -> fillLabels(operatorLabelsListStart, startOperator));
    }

    public void setEndOperator(ScanOperator endOperator) {
//        this.endOperator = endOperator;
        Platform.runLater(() -> fillLabels(operatorLabelsListEnd, endOperator));
    }

    private void fillLabels(List<Label> labelList, ScanOperator operator) {
        for (int i = 0; i < operator.getUarfcnList().size(); i++) {
            labelList.get(i).setText(operator.getUarfcnList().get(i).toString());
        }
        for (int i = 0, j = 3; i < operator.getLacList().size(); i++, j++) {
            labelList.get(j).setText(operator.getLacList().get(i).toString());
        }
        for (int i = 0, j = 6; i < operator.getRxList().size(); i++, j++) {
            labelList.get(j).setText(operator.getRxList().get(i).toString());
        }
        labelList.get(9).setText(operator.getTime());
        operatorNum.setText(String.valueOf(operator.getIdOper()));

        LOGGER.info("fill GUI form from operator: " + operator.getTime());
    }

    @Override
    public String toString() {
        return "AlarmController{" +
                "id=" + id +
                '}';
    }
}