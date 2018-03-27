package sample.task;


import javafx.concurrent.Task;
import sample.xmodem.Xmodem;

import java.io.File;
import java.net.PortUnreachableException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SaveTask extends Task {
    public final static String COMMAND_SAVE_TXT = "AT+DOWN_LOGS=";
    public final static String COMMAND_SAVE_ZIP = "AT+DOWN_LOGS=ALL";
    private File file;
//    private static int fileNameInd;

    public SaveTask(File file) {
        this.file = file;
    }

    @Override
    protected Object call() throws PortUnreachableException {
        try {
//            while (true) {
            List<File> fileList = createFiles(file);
            List<String> commandList = createCommands(file);

            for (int i = 0; i < fileList.size(); i++) {
                updateProgress(i, fileList.size());
                if (Xmodem.DEBUG) System.out.print("\n" + fileList.get(i));
                Xmodem.receiveFile(fileList.get(i), commandList.get(i));
            }
            updateProgress(fileList.size(), fileList.size());
            TimeUnit.MILLISECONDS.sleep(300);

//                fileNameInd++;
//            }
        } catch (InterruptedException e) {
            if (Xmodem.DEBUG) e.printStackTrace();
        }
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
