package sample.main;


import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;

public class ProgressController {
    @FXML
    private ProgressBar progressBar;

    @FXML
    private void initialize() {
    }

    public void setTask(Task task) {
        progressBar.progressProperty().bind(task.progressProperty());
    }
}
