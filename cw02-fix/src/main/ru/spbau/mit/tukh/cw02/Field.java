package main.ru.spbau.mit.tukh.cw02;

import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.paint.Paint;

import java.util.ArrayList;
import java.util.Random;

/**
 * Class, which describes field and game logic.
 */
public class Field {
    private int activeCnt;
    private State gameState;
    private int field[][];
    private ArrayList<Integer> freeCells;
    private Random random;
    private Button[][] buttons;
    private int lastPressedRow;
    private int lastPressedColumn;

    Field(int size, Button[][] buttons) {
        this.buttons = buttons;
        field = new int[size][size];
        this.gameState = State.WAITING_FOR_FIRST;

        int maxNumber = size * size / 2;
        freeCells = new ArrayList<>();
        for (int i = 0; i < 2 * maxNumber; i++) {
            freeCells.add(i);
        }
        random = new Random();

        for (int i = 0; i < maxNumber; i++) {
            int ind1 = generateRandomCell();
            int ind2 = generateRandomCell();
            field[ind1 / size][ind1 % size] = i;
            field[ind2 / size][ind2 % size] = i;
        }
        activeCnt = maxNumber;
    }

    int generateRandomCell() {
        int ind = random.nextInt(freeCells.size());
        int ans = freeCells.get(ind);
        freeCells.remove(ind);
        return ans;
    }

    /**
     * Method, which process click at one of the field cells.
     *
     * @param row    is a number of click row.
     * @param column is a number of click column.
     */
    void processClick(int row, int column) {
        if (field[row][column] == -1) {
            return;
        }

        switch (gameState) {
            case PAUSE:
                return;

            case OVER:
                return;

            case WAITING_FOR_FIRST:
                buttons[row][column].setText(Integer.toString(field[row][column]));
                buttons[row][column].setTextFill(Paint.valueOf("DARKGREEN"));
                lastPressedColumn = column;
                lastPressedRow = row;
                buttons[row][column].setDisable(true);
                gameState = State.WAITING_FOR_SECOND;
                break;

            case WAITING_FOR_SECOND:
                buttons[row][column].setDisable(true);
                buttons[row][column].setTextFill(Paint.valueOf("DARKGREEN"));
                buttons[row][column].setText(Integer.toString(field[row][column]));
                if (field[lastPressedRow][lastPressedColumn] == field[row][column]) {
                    buttons[row][column].setTextFill(Paint.valueOf("DARKBLUE"));
                    buttons[lastPressedRow][lastPressedColumn].setTextFill(Paint.valueOf("DARKBLUE"));
                    field[row][column] = -1;
                    field[lastPressedRow][lastPressedColumn] = -1;
                    activeCnt--;
                    if (activeCnt == 0) {
                        gameState = State.OVER;
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("You win!");
                        alert.setHeaderText("Congratulations,");
                        alert.setContentText("You win!");
                        alert.showAndWait();
                    } else {
                        gameState = State.WAITING_FOR_FIRST;
                    }
                } else {
                    gameState = State.PAUSE;
                    Task<Void> task = new Task<Void>() {
                        @Override
                        public Void call() throws Exception {
                            Thread.sleep(1000);
                            return null;
                        }
                    };
                    task.setOnSucceeded(event -> {
                        buttons[lastPressedRow][lastPressedColumn].setDisable(false);
                        buttons[lastPressedRow][lastPressedColumn].setTextFill(Paint.valueOf("black"));
                        buttons[row][column].setDisable(false);
                        buttons[row][column].setText("?");
                        buttons[lastPressedRow][lastPressedColumn].setText("?");
                        gameState = State.WAITING_FOR_FIRST;
                    });
                    new Thread(task).start();
                }
                break;
        }
    }

    /**
     * Enum for states of the game.
     */
    public enum State {
        WAITING_FOR_FIRST, WAITING_FOR_SECOND, OVER, PAUSE
    }
}
