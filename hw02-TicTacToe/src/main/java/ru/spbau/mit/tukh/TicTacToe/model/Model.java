package ru.spbau.mit.tukh.TicTacToe.model;

import java.util.Arrays;

public class Model {
    public enum CellState {
        X, O, NOTHING;

        public static CellState getCellStateByTurnStatus(final int turnStatus) {
            if (turnStatus == 0) {
                return CellState.X;
            }
            return CellState.O;
        }
    }

    private int turnStatus;
    private int lastModifiedX;
    private int lastModifiedY;
    private boolean isDraw;

    private CellState[][] field = new CellState[3][3];

    public Model() {
        for (CellState[] row: field) {
            Arrays.fill(row, CellState.NOTHING);
        }
    }

    public boolean processTurn(int row, int column) {
        if (field[row][column] != CellState.NOTHING || isFinished()) {
            return false;
        }
        field[row][column] = CellState.getCellStateByTurnStatus(turnStatus);
        lastModifiedX = row;
        lastModifiedY = column;
        turnStatus = 1 - turnStatus;
        return true;
    }

    public CellState getCellState(int row, int column) {
        return field[row][column];
    }

    public boolean isFinished() {
        boolean check = false;

        for (int i = 0; i < 3; i++) {
            check |= checkWin(i, 0, 0, 1);
            check |= checkWin(0, i, 1, 0);
        }

        check |= checkWin(0, 0, 1, 1);
        check |= checkWin(0, 2, 1, -1);

        int count = 0;
        for (int row = 0; row < 3; row++) {
            for(int column = 0; column < 3; column++) {
                count += field[row][column] == CellState.NOTHING ? 1 : 0;
            }
        }

        if (!check && count == 0) {
            isDraw = true;
        }
        return check || (count == 0);
    }

    private boolean checkWin(int startX, int startY, int dX, int dY) {
        int x = startX;
        int y = startY;

        boolean check = field[x][y] != CellState.NOTHING;
        for (int i = 0; i < 2; i++) {
            x += dX;
            y += dY;

            check &= field[x][y] == field[startX][startY];
        }

        return check;
    }

    public int getLastModifiedCellRow() {
        return lastModifiedX;
    }

    public int getLastModifiedCellColumn() {
        return lastModifiedY;
    }

    public boolean isDraw() {
        return isDraw;
    }
}