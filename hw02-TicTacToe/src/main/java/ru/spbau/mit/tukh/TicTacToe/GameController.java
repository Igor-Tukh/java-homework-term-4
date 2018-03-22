package ru.spbau.mit.tukh.TicTacToe;

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.GridPane;
import ru.spbau.mit.tukh.TicTacToe.bot.Bot;
import ru.spbau.mit.tukh.TicTacToe.bot.CleverBot;
import ru.spbau.mit.tukh.TicTacToe.bot.RandomBot;
import ru.spbau.mit.tukh.TicTacToe.model.Model;

public class GameController {
    private static Scene scene;
    private static GridPane gridPane;
    private Model model;
    private GameMode gameMode = GameMode.HOT_SEAT;
    private Bot bot;
    private int winnerNumber;
    private int currentPlayerNumber;
    private boolean firstTurnByBot;
    private boolean needToProcessFirstTurn = false;
    private boolean gameIsActive = false;
    private int playerVictoryCounter;
    private int botVictoryCounter;
    private int drawCounter;
    private int gamesCounter;

    public static void addScene(Scene scene) {
        GameController.scene = scene;
        gridPane = (GridPane) scene.lookup("#field");
    }

    public void onStatisticsPressed() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Statistics");
        alert.setHeaderText("Previous games in current game session statistics");
        alert.setContentText("Total played " + gamesCounter + " games.\nBot've won " + botVictoryCounter + " times,\n" +
                "Player've won " + playerVictoryCounter + " times,\nDraws number: " + drawCounter + ".");
        alert.show();
    }

    public void onButtonClick(ActionEvent actionEvent) {
        if (!gameIsActive) {
            return;
        }

        Button button = (Button) actionEvent.getTarget();
        int ind = button.getId().charAt(6) - '0';
        if (processTurn(ind / 3, ind % 3, button) == GameStatus.OVER) {
            String winner = getPlayersDescription(getWinnerNumber());
            playerVictoryCounter += winner.equals("Player") ? 1 : 0;
            botVictoryCounter += winner.equals("Bot") ? 1 : 0;
            drawCounter += model.isDraw() ? 1 : 0;
            gamesCounter++;
        }
    }

    public void onModeChose(ActionEvent actionEvent) {
        String mode = ((MenuItem) actionEvent.getTarget()).getId();

        switch (mode) {
            case "hotSeat":
                needToProcessFirstTurn = false;
                gameMode = GameMode.HOT_SEAT;
                break;
            case "easyBot":
                gameMode = GameMode.EASY_BOT;
                break;
            default:
                gameMode = GameMode.HARD_BOT;
                break;
        }
    }

    public void onGameOrderChose(ActionEvent actionEvent) {
        String order = ((MenuItem) actionEvent.getTarget()).getId();

        needToProcessFirstTurn = !(order.equals("playerBegins"));
    }

    public void startGame() {
        firstTurnByBot = needToProcessFirstTurn;
        currentPlayerNumber = 0;
        gameIsActive = true;
        this.model = new Model();
        updateStatus();

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                findButtonByCoordinates(row, column).setText("");
            }
        }


        if (gameMode == GameMode.EASY_BOT) {
            bot = new RandomBot();
        } else if (gameMode == GameMode.HARD_BOT) {
            bot = new CleverBot();
        }

        if (gameMode != GameMode.HOT_SEAT && needToProcessFirstTurn) {
            firstTurnByBot = true;
            bot.processTurn(model);
            currentPlayerNumber = 1;
            updateLastModified(findButtonByCoordinates(model.getLastModifiedCellRow(), model.getLastModifiedCellColumn()));
        }
    }

    private Button findButtonByCoordinates(int row, int column) {
        return (Button) gridPane.getChildren().get(row * 3 + column);
    }

    private void updateLastModified(Button button) {
        if (model.getCellState(model.getLastModifiedCellRow(), model.getLastModifiedCellColumn()) == Model.CellState.X) {
            button.setText("X");
        } else {
            button.setText("O");
        }
    }

    private GameStatus processTurn(int row, int column, Button button) {
        if (model.processTurn(row, column)) {
            updateLastModified(button);

            if (model.isFinished()) {
                winnerNumber = currentPlayerNumber;
                gameIsActive = false;

                updateStatus();
                return GameStatus.OVER;
            }

            currentPlayerNumber = 1 - currentPlayerNumber;

            if (gameMode != GameMode.HOT_SEAT) {
                bot.processTurn(model);
                updateLastModified(findButtonByCoordinates(model.getLastModifiedCellRow(), model.getLastModifiedCellColumn()));

                if (model.isFinished()) {
                    winnerNumber = currentPlayerNumber;
                    gameIsActive = false;
                    updateStatus();
                    return GameStatus.OVER;
                }

                currentPlayerNumber = 1 - currentPlayerNumber;
            }
        }

        updateStatus();
        return GameStatus.CONTINUES;
    }

    private void updateStatus() {
        Label label = (Label) scene.lookup("#status");
        if (model.isDraw()) {
            label.setText("Draw");
        } else if (model.isFinished()) {
            label.setText(getPlayersDescription(getWinnerNumber()) + " win.");
        } else {
            label.setText(getPlayersDescription(getCurrentPlayerNumber()) + "'s turn.");
        }
    }

    private int getWinnerNumber() {
        return winnerNumber;
    }

    private int getCurrentPlayerNumber() {
        return currentPlayerNumber;
    }

    private String getPlayersDescription(int playerNumber) {
        if (gameMode == GameMode.HOT_SEAT) {
            if (playerNumber == 0) {
                return "First player";
            } else {
                return "Second player";
            }
        } else {
            int number = playerNumber;
            if (firstTurnByBot) {
                number = 1 - playerNumber;
            }

            if (number == 0) {
                return "Player";
            } else {
                return "Bot";
            }
        }
    }

    public enum GameMode {HOT_SEAT, EASY_BOT, HARD_BOT}

    public enum GameStatus {CONTINUES, OVER}
}
