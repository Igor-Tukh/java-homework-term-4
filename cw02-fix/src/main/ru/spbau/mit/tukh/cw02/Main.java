package main.ru.spbau.mit.tukh.cw02;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Main application class.
 * It is an implementation of "Find pair" game.
 * Here size of a field should be given as an argument (default value is 4).
 * Correct size should be even and 2 <= size <= 16.
 */
public class Main extends Application {
    private static int size = 4;

    /**
     * Runs application.
     *
     * @param args should contain at most one argument, field size (by default 4).
     */
    public static void main(String[] args) {
        if (args.length > 1) {
            System.err.println("Wrong number of arguments");
            System.exit(0);
        }
        if (args.length > 0) {
            size = Integer.parseInt(args[0]);
        }

        if (size <= 0 || size % 2 != 0 || size > 16) {
            System.err.println("Size should be even and positive and not bigger, then 16"); // If i will have time, there will be exception
            System.exit(0);
        }

        runTests();
        launch(args);
    }

    private static void runTests() {
        Button[][] buttons = new Button[0][0];
        Field field = new Field(6, buttons);
        for (int i = 0; i < size * size * 10; i++) {
            assert(field.generateRandomCell() < size * size && field.generateRandomCell() >= 0);
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Find pairs");
        Scene scene = new Scene(root, 700, 700);
        primaryStage.setMinHeight(700);
        primaryStage.setMinWidth(700);
        GridPane gridPane = (GridPane) scene.lookup("#gridPane");
        Button[][] buttons = new Button[size][size];

        for (int i = 0; i < size; i++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setFillHeight(true);
            rowConstraints.setVgrow(Priority.ALWAYS);
            gridPane.getRowConstraints().add(rowConstraints);

            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setFillWidth(true);
            columnConstraints.setHgrow(Priority.ALWAYS);
            gridPane.getColumnConstraints().add(columnConstraints);
        }

        Field field = new Field(size, buttons);

        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                buttons[row][column] = new Button();
                buttons[row][column].setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                buttons[row][column].setText("?");
                buttons[row][column].setFont(Font.font(20));
                int finalRow = row;
                int finalColumn = column;
                buttons[row][column].setOnAction(event -> field.processClick(finalRow, finalColumn));
                gridPane.add(buttons[row][column], row, column);
            }
        }

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
