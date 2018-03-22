package ru.spbau.mit.tukh.TicTacToe.bot;

import ru.spbau.mit.tukh.TicTacToe.model.Model;

import java.util.ArrayList;
import java.util.Random;

public class RandomBot implements Bot {
    private Random random = new Random();

    @Override
    public void processTurn(Model model) {
        if (model.isFinished()) {
            return;
        }

        ArrayList<Integer> freeCells = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (model.getCellState(i, j) == Model.CellState.NOTHING) {
                    freeCells.add(i * 3 + j);
                }
            }
        }

        int ind = random.nextInt(freeCells.size());

        model.processTurn(freeCells.get(ind) / 3, freeCells.get(ind) % 3);
    }
}
