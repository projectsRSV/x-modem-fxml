package sample.main;

import com.fazecast.jSerialComm.SerialPort;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import sample.command.*;
import sample.exceptions.ListIsEmptyException;
import sample.parsers.OperParser;
import sample.parsers.OperSetParser;
import sample.parsers.UarfcnParser;
import sample.task.SaveTask;
import sample.task.ScanThread;
import sample.utils.I18N;
import sample.xmodem.Uart;
import sample.xmodem.Xmodem;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.PortUnreachableException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

//-javaagent:c:\ScenicView.jar
public class MainController {
    private final static Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    @FXML
    private Label operatorLabel1;
    @FXML
    private Label operatorLabel2;
    @FXML
    private Label operatorLabel3;
    @FXML
    private Label operatorLabel4;
    @FXML
    private CheckBox checkBoxOp1;
    @FXML
    private CheckBox checkBoxOp2;
    @FXML
    private CheckBox checkBoxOp3;
    @FXML
    private CheckBox checkBoxOp4;
    @FXML
    private TextField longNameOp1;
    @FXML
    private TextField longNameOp2;
    @FXML
    private TextField longNameOp3;
    @FXML
    private TextField longNameOp4;
    @FXML
    private TextField shortNameOp1;
    @FXML
    private TextField shortNameOp2;
    @FXML
    private TextField shortNameOp3;
    @FXML
    private TextField shortNameOp4;
    @FXML
    private TextField fieldMCC1;
    @FXML
    private TextField fieldMCC2;
    @FXML
    private TextField fieldMCC3;
    @FXML
    private TextField fieldMCC4;
    @FXML
    private Spinner<Integer> spinnerMNC1;
    @FXML
    private Spinner<Integer> spinnerMNC2;
    @FXML
    private Spinner<Integer> spinnerMNC3;
    @FXML
    private Spinner<Integer> spinnerMNC4;
    @FXML
    private CheckBox checkBoxGSM1;
    @FXML
    private CheckBox checkBoxGSM2;
    @FXML
    private CheckBox checkBoxGSM3;
    @FXML
    private CheckBox checkBoxGSM4;
    @FXML
    private CheckBox checkBoxWCDMA1;
    @FXML
    private CheckBox checkBoxWCDMA2;
    @FXML
    private CheckBox checkBoxWCDMA3;
    @FXML
    private CheckBox checkBoxWCDMA4;
    @FXML
    private ToggleButton connectButton;
    @FXML
    private ComboBox comboBox;
    @FXML
    private Button writeButton;
    @FXML
    private Button readButton;
    @FXML
    private Button delButton;
    @FXML
    private ToggleButton scanButton;
    @FXML
    private Label notification;
    @FXML
    private Menu saveMenu;
    @FXML
    private MenuItem saveTxt;
    //    @FXML
//    private MenuItem saveZip;
    @FXML
    private Menu optionMenu;
    @FXML
    private Menu languageMenu;
    @FXML
    private RadioMenuItem engItemMenu;
    @FXML
    private RadioMenuItem rusItemMenu;
    @FXML
    private CheckMenuItem timeSynchMenuItem;
    @FXML
    private MenuItem scanningMenuItem;

    private OperGUIList guiLists;
    private List<MakeCommandInt> listUarfcn = Arrays.asList(new CommandUarfcn(),
            new CommandUarfcn(), new CommandUarfcn(), new CommandUarfcn());
    private Uart uart;
    private ScheduledExecutorService executorService;
    Thread thread;
    ScanThread task;

    @FXML
    private void initialize() {
        guiLists = new OperGUIList();
        guiLists.setLongNameFieldList(Arrays.asList(longNameOp1, longNameOp2, longNameOp3, longNameOp4));
        guiLists.setShortNameFieldList(Arrays.asList(shortNameOp1, shortNameOp2, shortNameOp3, shortNameOp4));
        guiLists.setMccNameFieldList(Arrays.asList(fieldMCC1, fieldMCC2, fieldMCC3, fieldMCC4));
        guiLists.setSpinnerList(Arrays.asList(spinnerMNC1, spinnerMNC2, spinnerMNC3, spinnerMNC4));
        guiLists.setGsmCheckBoxList(Arrays.asList(checkBoxGSM1, checkBoxGSM2, checkBoxGSM3, checkBoxGSM4));
        guiLists.setWcdmaCheckBoxList(Arrays.asList(checkBoxWCDMA1, checkBoxWCDMA2, checkBoxWCDMA3, checkBoxWCDMA4));

        scanAndConnectToDevice();
        createMenuBar();
        createMainField();
    }

    @FXML
    private void makeLabelActive(MouseEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("uarfcn.fxml"));
        Stage uarfcnStage = new Stage();
        uarfcnStage.getIcons().add(new Image("/images/antenna.png"));
        uarfcnStage.setResizable(false);
        uarfcnStage.setX(event.getScreenX() - 50);
        uarfcnStage.setY(event.getScreenY() + 20);
        Parent parent = null;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Label operLabel = (Label) event.getSource();
        String idStr = operLabel.getId();
        int numEvent = Integer.parseInt(String.valueOf(idStr.charAt(idStr.length() - 1)));
        UarfcnController uarfcnController = loader.getController();
        uarfcnController.setOperForm((CommandUarfcn) listUarfcn.get(numEvent - 1));
        Scene scene = new Scene(parent, 196, 175);
        scene.getStylesheets().add("/css/GUI.css");
        uarfcnStage.initModality(Modality.APPLICATION_MODAL);
        uarfcnStage.setScene(scene);
        uarfcnStage.showAndWait();
    }

    @FXML
    private void scaleHandlerOn(Event event) {
        Label label = (Label) event.getSource();
        label.setScaleX(1.2);
        label.setScaleY(1.3);
    }

    @FXML
    private void scaleHandlerOff(Event event) {
        Label label = (Label) event.getSource();
        label.setScaleX(1.0);
        label.setScaleY(1.0);
    }

    @FXML
    private void writeButtonHandler() {
        Xmodem.read(1000, uart.getPort().bytesAvailable());
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                int max = 10;
                List<MakeCommandInt> listMain = new ArrayList<>();
                CommandOper operForm;
                updateProgress(0, max);
                if (checkBoxOp1.isSelected()) {
                    operForm = new CommandOper();
                    operForm.setIndex(1);
                    operForm.setLongName(longNameOp1.getText());
                    operForm.setShortName(shortNameOp1.getText());
                    operForm.setMcc(fieldMCC1.getText());
                    operForm.setMnc(spinnerMNC1.getValue().byteValue());
                    operForm.setGsm(checkBoxGSM1.isSelected() ? 1 : 0);
                    operForm.setWcdma(checkBoxWCDMA1.isSelected() ? 1 : 0);
                    listMain.add(operForm);
                }
                if (checkBoxOp2.isSelected()) {
                    operForm = new CommandOper();
                    operForm.setIndex(2);
                    operForm.setLongName(longNameOp2.getText());
                    operForm.setShortName(shortNameOp2.getText());
                    operForm.setMcc(fieldMCC2.getText());
                    operForm.setMnc(spinnerMNC2.getValue().byteValue());
                    operForm.setGsm(checkBoxGSM2.isSelected() ? 1 : 0);
                    operForm.setWcdma(checkBoxWCDMA2.isSelected() ? 1 : 0);
                    listMain.add(operForm);
                }
                if (checkBoxOp3.isSelected()) {
                    operForm = new CommandOper();
                    operForm.setIndex(3);
                    operForm.setLongName(longNameOp3.getText());
                    operForm.setShortName(shortNameOp3.getText());
                    operForm.setMcc(fieldMCC3.getText());
                    operForm.setMnc(spinnerMNC3.getValue().byteValue());
                    operForm.setGsm(checkBoxGSM3.isSelected() ? 1 : 0);
                    operForm.setWcdma(checkBoxWCDMA3.isSelected() ? 1 : 0);
                    listMain.add(operForm);
                }
                if (checkBoxOp4.isSelected()) {
                    operForm = new CommandOper();
                    operForm.setIndex(4);
                    operForm.setLongName(longNameOp4.getText());
                    operForm.setShortName(shortNameOp4.getText());
                    operForm.setMcc(fieldMCC4.getText());
                    operForm.setMnc(spinnerMNC4.getValue().byteValue());
                    operForm.setGsm(checkBoxGSM4.isSelected() ? 1 : 0);
                    operForm.setWcdma(checkBoxWCDMA4.isSelected() ? 1 : 0);
                    listMain.add(operForm);
                }
                updateProgress(1, max);
                List<MakeCommandInt> operList = new ArrayList<>();
                List<MakeCommandInt> listUarfcnValid = new ArrayList<>();
                for (MakeCommandInt o : listMain) {
                    if (o.isValid()) operList.add(o);
                    else throw new ListIsEmptyException();
                }
                for (MakeCommandInt o : listUarfcn) {
                    if (o.isValid()) listUarfcnValid.add(o);
                }
                if (!uart.getPort().isOpen()) throw new NullPointerException();

                Set<String> acknowledgeSet;
                for (MakeCommandInt m : operList) {
                    do {
                        SendCommand.send(m.getCommand());
                        acknowledgeSet = OperSetParser.parseData(Xmodem.read(2000, 285));
                    } while (!acknowledgeSet.contains("OK") && !acknowledgeSet.contains("--- SET OPERATOR ---"));
                }
                updateProgress(3, max);
                do {
                    SendCommand.send(Commands.DEL_FREQ.getCommand());
                    acknowledgeSet = OperSetParser.parseData(Xmodem.read(1000, 27));
                } while (!acknowledgeSet.contains("OK") && !acknowledgeSet.contains("3G base file removed."));
                updateProgress(5, max);
                for (MakeCommandInt m : listUarfcn) {
                    if (!m.getCommand().equals("")) {
                        do {
                            SendCommand.send(m.getCommand());
                            acknowledgeSet = OperSetParser.parseData(Xmodem.read(1000, 21));
                        } while (!acknowledgeSet.contains("OK") && !acknowledgeSet.contains("Adding to Base!"));
                    }
                }
                updateProgress(7, max);
                do {
                    SendCommand.send(Commands.REFRESH.getCommand());
                    acknowledgeSet = OperSetParser.parseData(Xmodem.read(1000, 4));
                } while (!acknowledgeSet.contains("OK"));
                updateProgress(9, max);
                if (timeSynchMenuItem.isSelected()) {
                    do {
                        SendCommand.send(Commands.SET_CLOCK.getCommand());
                        acknowledgeSet = OperSetParser.parseData(Xmodem.read(1000, 6));
                    } while (!acknowledgeSet.contains("OK"));
                }
                updateProgress(max, max);
                TimeUnit.MILLISECONDS.sleep(300);
                return null;
            }
        };
        createProgressBar(task);
    }

    @FXML
    private void delScans() {
        executorService = Executors.newScheduledThreadPool(1);
        executorService.submit(() -> {
            Xmodem.read(1000, uart.getPort().bytesAvailable());
            try {
                Set<String> acknowledgeSet;
                do {
                    SendCommand.send(Commands.DEL_SCANS.getCommand());
                    acknowledgeSet = OperSetParser.parseData(Xmodem.read(1000, 11));
                } while (!acknowledgeSet.contains("OK") && !acknowledgeSet.contains("Done!"));
                LOGGER.info("remove scans");
            } catch (PortUnreachableException e) {
                e.printStackTrace();
            }
        });
        executorService.shutdown();
    }

    @FXML
    private void scanOperators(ActionEvent event) {
        ToggleButton button = (ToggleButton) event.getSource();
        if (button.isSelected()) {
            task = new ScanThread();
            task.setUart(uart);
            task.setPriStage((Stage) readButton.getScene().getWindow());
            executorService = Executors.newScheduledThreadPool(1);
            LOGGER.info("press scan button");
//            executorService.scheduleWithFixedDelay(task, 0, 1, TimeUnit.SECONDS);
            executorService.submit(task);
            readButton.setDisable(true);
            writeButton.setDisable(true);
            delButton.setDisable(true);
        } else {
            readButton.setDisable(false);
            writeButton.setDisable(false);
            delButton.setDisable(false);
            LOGGER.info("release scan button");
//            thread.interrupt();
//            executorService.shutdown();
            task.interrrupt();
        }
    }

    @FXML
    private void readButtonHandler() {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                if (!uart.getPort().isOpen()) throw new NullPointerException();
                Xmodem.read(1000, uart.getPort().bytesAvailable());
                SendCommand.send(Commands.READ_FREQ.getCommand());
                updateProgress(1, 5);
                UarfcnParser uarfcnParser = new UarfcnParser(listUarfcn);
                uarfcnParser.parseData(Xmodem.read(1000, 237));
                SendCommand.send(Commands.READ_MAIN.getCommand());
                updateProgress(3, 5);
                OperParser operParser = new OperParser(guiLists);
                operParser.parseData(Xmodem.read(1000, 261));
                updateProgress(5, 5);
                return null;
            }
        };
        createProgressBar(task);
    }

    private void createProgressBar(Task task) {
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit(task);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("progressBar.fxml"));
        Stage progressStage = new Stage();
        Stage priStage = (Stage) readButton.getScene().getWindow();
        progressStage.setX(priStage.getX() + 15);
        progressStage.setY(priStage.getY() + 100);
        progressStage.setOnCloseRequest(event -> service.shutdownNow());
        task.setOnSucceeded(event1 -> {
            setAlarmMessage("null");
            progressStage.close();
        });
        task.setOnFailed(event1 -> {
            if (task.getException().toString().contains("ListIsEmptyException")) {
                setAlarmMessage("name.long");
            } else if (task.getException().toString().contains("RebootDeviceException")) {
                connectButton.setSelected(false);
                connectButton.textProperty().bind(I18N.createStringBinding("button.connect"));
                setAlarmMessage("reboot.device");
            } else if (task.getException().toString().contains("NullPointerException")) {
                setAlarmMessage("null");
            } else {
                LOGGER.log(Level.SEVERE, "return exception from task", task.getException());
                setAlarmMessage("connect.device");
            }
            progressStage.close();
        });

        progressStage.initStyle(StageStyle.DECORATED);
        progressStage.setResizable(false);
        Parent parent = null;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ProgressController progressController = loader.getController();
        progressController.setTask(task);
        Scene scene = new Scene(parent, 270, 10);
        scene.getStylesheets().add("/css/GUI.css");
        progressStage.initModality(Modality.APPLICATION_MODAL);
        progressStage.setScene(scene);
        progressStage.show();
    }

    @FXML
    private void saveFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.titleProperty().bind(I18N.createStringBinding("save.title"));
        if (((MenuItem) event.getSource()).getText().endsWith(".txt")) fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(I18N.get("type.files.txt"), "*.txt"));
        else fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(I18N.get("type.files.zip"), "*.zip"));
        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            SaveTask saveTask = new SaveTask(file);
            createProgressBar(saveTask);
        }
    }

    private void createMenuBar() {
        saveMenu.getStyleClass().add("menu-my");
        saveMenu.textProperty().bind(I18N.createStringBinding("menu.title"));
        saveTxt.textProperty().bind(I18N.createStringBinding("menu.save.txt"));
//        saveZip.textProperty().bind(I18N.createStringBinding("menu.save.zip"));
        optionMenu.textProperty().bind(I18N.createStringBinding("menu.option"));
        languageMenu.textProperty().bind(I18N.createStringBinding("menu.lang"));
        engItemMenu.textProperty().bind(I18N.createStringBinding("menu.item.eng"));
        engItemMenu.setOnAction(event -> I18N.setLocale(I18N.getSupportedLocales().get(0)));
        rusItemMenu.textProperty().bind(I18N.createStringBinding("menu.item.rus"));
        rusItemMenu.setOnAction(event -> I18N.setLocale(I18N.getSupportedLocales().get(1)));
        if (Locale.getDefault().toString().equals("ru_RU")) rusItemMenu.setSelected(true);
        else engItemMenu.setSelected(true);
        timeSynchMenuItem.textProperty().bind(I18N.createStringBinding("menu.timeSynch"));
    }

    private void setAlarmMessage(String string) {
        notification.textProperty().bind(I18N.createStringBinding(string));
    }

    @FXML
    private void scanAndConnectToDevice() {
        uart = new Uart();
        comboBox.setItems(uart.getPortListString());
        Callback cellFactory = new Callback<ListView<SerialPort>, ListCell<SerialPort>>() {
            @Override
            public ListCell<SerialPort> call(ListView<SerialPort> param) {
                return new ListCell<SerialPort>() {
                    @Override
                    protected void updateItem(SerialPort item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(item.getDescriptivePortName());
                        }
                    }
                };
            }
        };
        comboBox.valueProperty().addListener((observable, oldValue, newValue) -> uart.setPort((SerialPort) newValue));
        comboBox.setCellFactory(cellFactory);
        comboBox.setButtonCell((ListCell) cellFactory.call(null));
    }

    @FXML
    private void connectHandler(ActionEvent event) {
        ToggleButton button = (ToggleButton) event.getSource();
        if (button.isSelected()) {
            try {
                uart.openPortDevice();
                Xmodem.setSerialPort(uart.getPort());
//                button.setSelected(true);
                button.textProperty().bind(I18N.createStringBinding("button.disconnect"));
                new Thread(() -> Xmodem.read(2000,
                        uart.getPort().bytesAvailable())).start();
                setAlarmMessage("null");
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "cannot open port", e);
                button.setSelected(false);
                button.textProperty().bind(I18N.createStringBinding("button.connect"));
                setAlarmMessage("connect.device");
            }
        } else {
            uart.getPort().closePort();
            Xmodem.setSerialPort(null);
            button.textProperty().bind(I18N.createStringBinding("button.connect"));
//            button.setSelected(false);
            LOGGER.info("port is closed");
        }
    }

    private void createMainField() {
        scanningMenuItem.textProperty().bind(I18N.createStringBinding("menu.scan"));
        connectButton.textProperty().bind(I18N.createStringBinding("button.connect"));
        connectButton.setOnMouseEntered(event -> connectButton.setEffect(new Lighting()));
//        connectButton.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> connectButton.setEffect(new Lighting()));
        connectButton.setOnMouseExited(event -> connectButton.setEffect(null));

        readButton.textProperty().bind(I18N.createStringBinding("button.read"));
        readButton.setOnMouseEntered(event -> readButton.setEffect(new Lighting()));
        readButton.setOnMouseExited(event -> readButton.setEffect(null));
        Tooltip readTooltip = new Tooltip();
        readTooltip.textProperty().bind(I18N.createStringBinding("tooltip.read"));
        readButton.setTooltip(readTooltip);

        delButton.textProperty().bind(I18N.createStringBinding("button.delete"));
        delButton.setOnMouseEntered(event -> delButton.setEffect(new Lighting()));
        delButton.setOnMouseExited(event -> delButton.setEffect(null));
        Tooltip delTooltip = new Tooltip();
        delTooltip.textProperty().bind(I18N.createStringBinding("tooltip.delete"));
        delButton.setTooltip(delTooltip);

        writeButton.textProperty().bind(I18N.createStringBinding("button.write"));
        writeButton.setOnMouseEntered(event -> writeButton.setEffect(new Lighting()));
        writeButton.setOnMouseExited(event -> writeButton.setEffect(null));
        Tooltip writeTooltip = new Tooltip();
        writeTooltip.textProperty().bind(I18N.createStringBinding("tooltip.write"));
        writeButton.setTooltip(writeTooltip);

        scanButton.textProperty().bind(I18N.createStringBinding("button.scan"));
        scanButton.setOnMouseEntered(event -> scanButton.setEffect(new Lighting()));
        scanButton.setOnMouseExited(event -> scanButton.setEffect(null));
        Tooltip scanTooltip = new Tooltip();
        scanTooltip.textProperty().bind(I18N.createStringBinding("tooltip.scan"));
        scanButton.setTooltip(scanTooltip);

        notification.getStyleClass().add("label-notif-main");
        operatorLabel1.getStyleClass().add("label-operator-main-norm");
        operatorLabel2.getStyleClass().add("label-operator-main-norm");
        operatorLabel3.getStyleClass().add("label-operator-main-norm");
        operatorLabel4.getStyleClass().add("label-operator-main-norm");

        Tooltip tooltip = new Tooltip();
        tooltip.textProperty().bind(I18N.createStringBinding("tooltip.operator"));
        operatorLabel1.setTooltip(tooltip);
        operatorLabel2.setTooltip(tooltip);
        operatorLabel3.setTooltip(tooltip);
        operatorLabel4.setTooltip(tooltip);

        operatorLabel1.textProperty().bind(I18N.createStringBinding("title.operator", 1));
        operatorLabel2.textProperty().bind(I18N.createStringBinding("title.operator", 2));
        operatorLabel3.textProperty().bind(I18N.createStringBinding("title.operator", 3));
        operatorLabel4.textProperty().bind(I18N.createStringBinding("title.operator", 4));
        longNameOp1.promptTextProperty().bind(I18N.createStringBinding("prompt.long.text"));
        longNameOp2.promptTextProperty().bind(I18N.createStringBinding("prompt.long.text"));
        longNameOp3.promptTextProperty().bind(I18N.createStringBinding("prompt.long.text"));
        longNameOp4.promptTextProperty().bind(I18N.createStringBinding("prompt.long.text"));
        shortNameOp1.promptTextProperty().bind(I18N.createStringBinding("prompt.short.text"));
        shortNameOp2.promptTextProperty().bind(I18N.createStringBinding("prompt.short.text"));
        shortNameOp3.promptTextProperty().bind(I18N.createStringBinding("prompt.short.text"));
        shortNameOp4.promptTextProperty().bind(I18N.createStringBinding("prompt.short.text"));

        SpinnerValueFactory<Integer> valueFactory1 = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99, 1);
        SpinnerValueFactory<Integer> valueFactory2 = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99, 1);
        SpinnerValueFactory<Integer> valueFactory3 = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99, 1);
        SpinnerValueFactory<Integer> valueFactory4 = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99, 1);
        spinnerMNC1.setValueFactory(valueFactory1);
        spinnerMNC2.setValueFactory(valueFactory2);
        spinnerMNC3.setValueFactory(valueFactory3);
        spinnerMNC4.setValueFactory(valueFactory4);

        longNameOp1.setTextFormatter(limitString(14));
        longNameOp2.setTextFormatter(limitString(14));
        longNameOp3.setTextFormatter(limitString(14));
        longNameOp4.setTextFormatter(limitString(14));

        shortNameOp1.setTextFormatter(limitString(4));
        shortNameOp2.setTextFormatter(limitString(4));
        shortNameOp3.setTextFormatter(limitString(4));
        shortNameOp4.setTextFormatter(limitString(4));

        fieldMCC1.setTextFormatter(limitDigit(3));
        fieldMCC2.setTextFormatter(limitDigit(3));
        fieldMCC3.setTextFormatter(limitDigit(3));
        fieldMCC4.setTextFormatter(limitDigit(3));

        fieldMCC1.setPromptText("255");
        fieldMCC2.setPromptText("255");
        fieldMCC3.setPromptText("255");
        fieldMCC4.setPromptText("255");
    }

    private static TextFormatter<?> limitString(int i) {
        return new TextFormatter<>(change -> {
            if (!change.getText().isEmpty())
                return (change.getText().matches("\\w+") && change.getControlNewText().length() <= i) ? change : null;
            return change;
        });
    }

    public static TextFormatter<?> limitDigit(int i) {
        return new TextFormatter<>(change -> {
            if (!change.getText().isEmpty())
                return (change.getText().matches("\\d+") && change.getControlNewText().length() <= i) ? change : null;
            return change;
        });
    }
}
