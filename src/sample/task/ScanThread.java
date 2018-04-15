package sample.task;


import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.command.Commands;
import sample.command.ScanOperator;
import sample.command.SendCommand;
import sample.main.AlarmController;
import sample.parsers.OperSetParser;
import sample.parsers.ScanParser;
import sample.xmodem.Uart;
import sample.xmodem.Xmodem;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScanThread implements Runnable {
    private final static Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private Uart uart;
    private Stage priStage;

    private Map<Integer, AlarmController> controllerMap;
    private List<ScanOperator> scanList;
    private int windowStep;
    private volatile boolean stop;

    public void interrrupt() {
        stop = true;
        LOGGER.warning("start interrupt method: " + stop);
    }

    public ScanThread() {
        this.windowStep = 0;
        this.controllerMap = new HashMap<>();
        this.scanList = Arrays.asList(new ScanOperator(), new ScanOperator(), new ScanOperator(), new ScanOperator());
        for (ScanOperator operator : scanList) {
            operator.isCatcherProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.equals(true)) {
                    operator.setTime(new SimpleDateFormat("yyyy/MM/dd  HH:mm:ss").format(new Date()));
                    LOGGER.info(newValue + " property " + operator.getTime());
                } else {
                    operator.setTime(new SimpleDateFormat("yyyy/MM/dd  HH:mm:ss").format(new Date()));
                    LOGGER.info(oldValue + " property " + operator.getTime());
                }
                LOGGER.info("***********find catcher***********");
                Platform.runLater(() -> ScanThread.this.renderOperator(operator));
            });
        }
    }

    public void setUart(Uart uart) {
        this.uart = uart;
    }

    public void setPriStage(Stage priStage) {
        this.priStage = priStage;
    }

    @Override
    public void run() {
        int i = 1;
        LOGGER.info("1. thread of scanning start");
        Set<String> acknowledgeSet;
        while (!stop) {
            if (i >= 5) i = 1;
            try {
                ScanOperator operator = scanList.get(i - 1);
                Xmodem.read(1000, uart.getPort().bytesAvailable());
                do {
                    SendCommand.send(Commands.SCAN_OPER.getCommand() + i + ",3G");
                    acknowledgeSet = OperSetParser.parseData(Xmodem.read(2000, 47));
                } while (!acknowledgeSet.contains(String.format("Start scanning with operator #%d and mode 3G..", i)));
                LOGGER.info("2. receiving data over xmodem: " + stop);
                ScanParser.parse(operator, Xmodem.read(20000, 300));
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "exception in scan thread: " + i + " " + e.getClass().getName(), e);
            }
            i++;
        }
        LOGGER.info("3. scanning thread stop: " + stop);
    }

    private void renderOperator(ScanOperator operator) {
        try {
            AlarmController alarmController = controllerMap.get(operator.getIdOper());
            if (alarmController != null) {
                LOGGER.info("add operator to exist stage " + operator.getIdOper());
                LOGGER.info(alarmController.toString());
                alarmController.setEndOperator(operator);
                controllerMap.remove(operator.getIdOper());
            } else {
                LOGGER.info("create new stage " + operator.getIdOper());
                alarmController = createStage(operator);
                controllerMap.put(operator.getIdOper(), alarmController);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        AudioClip sound = new AudioClip(getClass().getResource("/sounds/appear.wav").toString());
        sound.play();
    }

    private AlarmController createStage(ScanOperator operator) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("alarmWindow.fxml"));
        Stage alarmStage = new Stage();
        alarmStage.getIcons().add(new Image("/images/alarm.png"));
        windowStep += 2;
        alarmStage.setX(priStage.getX() + 20 + windowStep);
        alarmStage.setY(priStage.getY() + 100 + windowStep);
        alarmStage.setTitle("Find catcher");
        Parent parent = loader.load();
        AlarmController alarmController = loader.getController();
        alarmController.setStartOperator(operator);
        Scene scene = new Scene(parent, 500, 190);
        scene.getStylesheets().add("/css/alarm.css");
        alarmStage.setScene(scene);
        alarmStage.initModality(Modality.NONE);
        alarmStage.setResizable(false);
        alarmStage.show();
        return alarmController;
    }
}
