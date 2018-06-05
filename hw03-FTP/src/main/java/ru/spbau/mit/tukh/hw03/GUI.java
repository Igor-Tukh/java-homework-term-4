package ru.spbau.mit.tukh.hw03;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GUI extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/GUI.fxml"));
        primaryStage.setTitle("FTP");
        Scene scene = new Scene(root, 200, 300);
        primaryStage.setMinHeight(300);
        primaryStage.setMinWidth(200);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
