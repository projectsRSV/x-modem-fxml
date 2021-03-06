package sample.task;


import javafx.concurrent.Task;
import sample.xmodem.Xmodem;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.net.PortUnreachableException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class SaveTask extends Task {
    private final static Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    public final static String COMMAND_SAVE_TXT = "AT+DOWN_LOGS=";
    public final static String COMMAND_SAVE_ZIP = "AT+DOWN_LOGS=ALL";
    private File file;

    public SaveTask(File file) {
        this.file = file;
    }

    @Override
    protected Object call() throws PortUnreachableException {
        List<File> fileList = createFiles(file);
        List<String> commandList = createCommands(file);

        for (int i = 0; i < fileList.size(); i++) {
            updateProgress(i, fileList.size());
            LOGGER.info("save to file: " + fileList.get(i).toString());
            Xmodem.receiveFile(fileList.get(i), commandList.get(i));
        }
        updateProgress(fileList.size(), fileList.size());
        return null;
    }

    private List<String> createCommands(File file) {
        List<String> list = new ArrayList<>();
        if (file.getAbsolutePath().endsWith(".txt")) {
            for (int i = 0; i < 4; i++) {
                list.add(COMMAND_SAVE_TXT + (i + 1));
            }
        } else list.add(COMMAND_SAVE_ZIP);
        return list;
    }

    private List<File> createFiles(File file) {
        List<File> fileList = new ArrayList<>();
        String strFile = file.getAbsolutePath();
        if (strFile.endsWith(".txt")) {
            for (int i = 0; i < 4; i++) {
                fileList.add(new File(strFile.substring(0, strFile.length() - 4) + (i + 1) +
                        strFile.substring(strFile.length() - 4, strFile.length())));
            }
        } else {
            fileList.add(file);
        }
        return fileList;
    }
}
