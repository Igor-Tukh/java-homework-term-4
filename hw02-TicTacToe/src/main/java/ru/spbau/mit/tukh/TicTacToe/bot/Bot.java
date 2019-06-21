package ru.spbau.mit.tukh.TicTacToe.bot;

import ru.spbau.mit.tukh.TicTacToe.model.Model;

/**
 * Interface for bots.
 */
public interface Bot {
    /**
     * Requests to do one turn in a given game. Do it, if games isn't over.
     *
     * @param model is model to do a turn.
     */
    void processTurn(Model model);
}
