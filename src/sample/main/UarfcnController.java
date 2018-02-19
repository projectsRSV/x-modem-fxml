package sample.main;


import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import sample.logic.I18N;
import sample.xmodem.Xmodem;
import sample.xmodem.commands.CommandUarfcn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UarfcnController {
    private static final int START_900 = 2937;
    private static final int END_900 = 3088;
    private static final int START_2100 = 10562;
    private static final int END_2100 = 10838;
    private CommandUarfcn operForm;
    @FXML
    private Label notificationLabel;
    @FXML
    private Button saveButton;
    @FXML
    private TextField uarfcnField1;
    @FXML
    private TextField uarfcnField2;
    @FXML
    private TextField uarfcnField3;
    @FXML
    private TextField uarfcnField4;
    List<TextField> textFieldList;

    @FXML
    public void initialize() {
        textFieldList = new ArrayList<>(Arrays.asList(uarfcnField1, uarfcnField2, uarfcnField3, uarfcnField4));
        notificationLabel.getStyleClass().add("label-notif-uarfcn");
        EventHandler<KeyEvent> enableBtnAndSetNotifHandler = event -> {
            TextField textField = (TextField) event.getSource();
            if (!isInRange(textField.getText())) {
                saveButton.setDisable(true);
                notificationLabel.textProperty().bind(I18N.createStringBinding("uarfcn.wrong"));
            } else {
                saveButton.setDisable(false);
                notificationLabel.textProperty().bind(I18N.createStringBinding("null"));
            }
        };
        for (TextField t : textFieldList) {
            t.setTextFormatter(Controller.limitDigit(5));
            t.setOnKeyReleased(enableBtnAndSetNotifHandler);
        }
        saveButton.setOnAction(event -> {
            operForm.setUarfcnList(new ArrayList<>());
            for (int i = 0; i < textFieldList.size(); i++) {
                if (!textFieldList.get(i).getText().isEmpty()) {
                    operForm.getUarfcnList().add(textFieldList.get(i).getText());
                }
            }
            if (Xmodem.DEBUG) System.out.println(operForm);
            ((Stage) saveButton.getScene().getWindow()).close();
        });
    }

    private boolean isInRange(String strNumber) {
        int intNum = 0;
        try {
            intNum = Integer.parseInt(strNumber);
        } catch (NumberFormatException e) {
            return true;
        }
        if ((intNum >= START_2100 && intNum <= END_2100) || (intNum >= START_900 && intNum <= END_900)) { return true;}
        return false;
    }

    public void setOperForm(CommandUarfcn operForm) {
        this.operForm = operForm;
        for (TextField t : textFieldList) {
            t.setText("");
        }
        try {
            for (int i = 0; i < operForm.getUarfcnList().size(); i++) {
                textFieldList.get(i).setText(operForm.getUarfcnList().get(i));
            }
        } catch (NullPointerException e) {
            if (Xmodem.DEBUG) System.out.println("nullPointerException");
        }
    }
}