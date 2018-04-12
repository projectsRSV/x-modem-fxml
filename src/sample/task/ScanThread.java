package sample.task;


import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.command.Commands;
import sample.command.ScanOperator;
import sample.command.SendCommand;
import sample.parsers.OperSetParser;
import sample.parsers.ScanParser;
import sample.xmodem.Uart;
import sample.xmodem.Xmodem;

import java.lang.invoke.MethodHandles;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScanThread implements Runnable {
    private final static Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    private Uart uart;
    private Stage priStage;
    private List<ScanOperator> scanList = Arrays.asList(new ScanOperator(), new ScanOperator(), new ScanOperator(), new ScanOperator());
    private int windowStep;
    private ScanOperator operator;
    private static int scanCount;

    public ScanThread() {
        windowStep = 0;
        scanCount = 0;
        for (ScanOperator operator : scanList) {
            operator.isCatcherProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.equals(true)) {
                    operator.setStartTime(new SimpleDateFormat("yy/MM/dd  HH:mm:ss").format(new Date()));
                    LOGGER.info(newValue + " property " + operator.getStartTime());
                } else {
                    operator.setEndTime(new SimpleDateFormat("yy/MM/dd  HH:mm:ss").format(new Date()));
                    LOGGER.info(newValue + " property " + operator.getEndTime());
                }
                LOGGER.info("***********find catcher***********");
                Platform.runLater(() -> ScanThread.this.showAlarmWindow(operator));
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
        LOGGER.info("2. thread of scanning start");
        Set<String> acknowledgeSet;
        try {
            if (scanCount++ >= 4)
                scanCount = 1;
            operator = scanList.get(scanCount - 1);
            Xmodem.read(1000, uart.getPort().bytesAvailable());
            do {
                SendCommand.send(Commands.SCAN_OPER.getCommand() + scanCount + ",3G");
                acknowledgeSet = OperSetParser.parseData(Xmodem.read(2000, 47));
            } while (!acknowledgeSet.contains(String.format("Start scanning with operator #%d and mode 3G..", scanCount)));
            LOGGER.info("before blocking receive");
            ScanParser.parse(operator, Xmodem.read(20000, 300));
            LOGGER.info("after blocking receive");
            LOGGER.info("3. thread of scanning stop");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "exception in scan thread", e);
        }
    }

    private void showAlarmWindow(ScanOperator operator) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning dialog");
        alert.setHeaderText("Find catcher");
        alert.setContentText("operator № "
                + operator.getIdOper() + "\n"
                + operator.getUarfcnList() + "\n"
                + operator.getLacList() + "\n"
                + operator.getCidList() + "\n"
                + operator.getRxList() + "\n"
                + operator.getStartTime() + "\n"
                + operator.getEndTime() + "\n"
                + operator.isIsCatcher());
        windowStep += 2;
        alert.setX(priStage.getX() + 100 + windowStep);
        alert.setY(priStage.getY() + 100 + windowStep);
        alert.initModality(Modality.NONE);
        alert.show();
    }
}
