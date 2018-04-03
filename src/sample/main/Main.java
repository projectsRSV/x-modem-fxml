package sample.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import sample.utils.I18N;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.LogManager;

public class Main extends Application {
    private static final LogManager logManager = LogManager.getLogManager();
    static {
        try {
            logManager.readConfiguration(new FileInputStream("resources/log.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        primaryStage.titleProperty().bind(I18N.createStringBinding("title.stage", 1, 5));
        primaryStage.getIcons().add(new Image("/css/antenna.png"));
        Scene scene = new Scene(root, 515, 315);
//        scene.getStylesheets().add("/css/GUI.css");
        scene.getStylesheets().add(getClass().getResource("/css/GUI.css").toExternalForm());
        primaryStage.setScene(scene);
        setUserAgentStylesheet(STYLESHEET_CASPIAN);
        primaryStage.setX(1370);
//        primaryStage.setX(770);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
