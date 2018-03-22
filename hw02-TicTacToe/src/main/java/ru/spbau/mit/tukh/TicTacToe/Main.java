package ru.spbau.mit.tukh.TicTacToe;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Tic-Tac-Toe application.
 */
public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/TicTacToe.fxml"));
        primaryStage.setTitle("Tic-Tac-Toe");
        Scene scene = new Scene(root, 500, 500);
        scene.getStylesheets().add("style.css");
        primaryStage.setMinHeight(500);
        primaryStage.setMinWidth(500);
        GameController.addScene(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
