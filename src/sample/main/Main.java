package sample.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import sample.logic.I18N;
import sample.xmodem.Xmodem;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.titleProperty().bind(I18N.createStringBinding("title.stage", 1, 4));
        primaryStage.getIcons().add(new Image("sample/css/antenna.png"));
        Scene scene = new Scene(root, 515, 315);
        scene.getStylesheets().add("sample/css/GUI.css");
        primaryStage.setScene(scene);
        setUserAgentStylesheet(STYLESHEET_CASPIAN);
        if (Xmodem.DEBUG) primaryStage.setX(1370);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
