package ru.spbau.mit.tukh.TicTacToe.bot;

import ru.spbau.mit.tukh.TicTacToe.model.Model;

/**
 * This bot is implementation of bot interface. It will do a turn in a cell which is a winning cell to opponent on the
 * next turn. But if there are more than one winning cell it will lose. Also use Manhattan distance to center heuristic.
 * But it can skip his own winning cell to more fun.
 */
public class CleverBot implements Bot {
    @Override
    public void processTurn(Model model) {
        if (model.isFinished()) {
            return;
        }

        int ind = -1;
        for (int i = 0; i < 3; i++) {
            if (canLose(model, i, 0, 0, 1)) {
                ind = findNothingByDirection(model, i, 0, 0, 1);
            }
            if (canLose(model, 0, i, 1, 0)) {
                ind = findNothingByDirection(model, 0, i, 1, 0);
            }
        }

        if (canLose(model, 0, 0, 1, 1)) {
            ind = findNothingByDirection(model, 0, 0, 1, 1);
        }
        if (canLose(model, 0, 2, 1, -1)) {
            ind = findNothingByDirection(model, 0, 2, 1, -1);
        }

        if (ind == -1) {
            int dist = 10;

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (model.getCellState(i, j) == Model.CellState.NOTHING) {
                        if (dist > distanceToCenter(i, j)) {
                            ind = i * 3 + j;
                            dist = distanceToCenter(i, j);
                        }
                    }
                }
            }
        }

        model.processTurn(ind / 3, ind % 3);
    }

    private int distanceToCenter(int x1, int y1) {
        return Math.abs(x1 - 1) + Math.abs(y1 - 1);
    }

    private boolean canLose(Model model, int startX, int startY, int dX, int dY) {
        int x = startX;
        int y = startY;

        int nothingCount = 0;
        int dangerCount = 0;

        for (int i = 0; i < 3; i++) {
            nothingCount += model.getCellState(x, y) == Model.CellState.NOTHING ? 1 : 0;
            dangerCount += model.getCellState(x, y) == Model.CellState.getCellStateByTurnStatus(1 - model.getTurnStatus()) ? 1 : 0;

            x += dX;
            y += dY;
        }

        return dangerCount == 2 && nothingCount == 1;
    }

    private int findNothingByDirection(Model model, int startX, int startY, int dX, int dY) {
        int x = startX;
        int y = startY;

        int ans = 0;
        for (int i = 0; i < 3; i++) {
            if (model.getCellState(x, y) == Model.CellState.NOTHING) {
                ans = 3 * x + y;
                break;
            }

            x += dX;
            y += dY;
        }

        return ans;
    }
}
