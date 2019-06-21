package ru.spbau.mit.tukh.TicTacToe.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class ModelTest {
    private Model model;

    private void init() {
        model = new Model();
    }

    @Test
    public void testGameFirstWins() {
        init();
        assertTrue(model.processTurn(1, 1));
        assertTrue(model.processTurn(0, 0));
        assertTrue(model.processTurn(0, 1));
        assertTrue(model.processTurn(2, 1));
        assertTrue(model.processTurn(1, 2));
        assertTrue(model.processTurn(2, 0));
        assertTrue(model.processTurn(1, 0));

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                assertFalse(model.processTurn(row, column));
            }
        }

        assertFalse(model.isDraw());
        assertTrue(model.isFinished());
        assertEquals(1, model.getLastModifiedCellRow());
        assertEquals(0, model.getLastModifiedCellColumn());
    }

    @Test
    public void testGameSecondWins() {
        init();
        assertTrue(model.processTurn(1, 1));
        assertTrue(model.processTurn(0, 0));
        assertTrue(model.processTurn(0, 1));
        assertTrue(model.processTurn(2, 1));
        assertTrue(model.processTurn(1, 2));
        assertTrue(model.processTurn(2, 0));
        assertTrue(model.processTurn(0, 2));
        assertTrue(model.processTurn(1, 0));

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                assertFalse(model.processTurn(row, column));
            }
        }

        assertFalse(model.isDraw());
        assertTrue(model.isFinished());
        assertEquals(1, model.getLastModifiedCellRow());
        assertEquals(0, model.getLastModifiedCellColumn());
    }

    @Test
    public void testGameDraw() {
        init();
        assertTrue(model.processTurn(1, 1));
        assertTrue(model.processTurn(1, 0));
        assertTrue(model.processTurn(0, 0));
        assertTrue(model.processTurn(2, 2));
        assertTrue(model.processTurn(2, 1));
        assertTrue(model.processTurn(0, 1));
        assertTrue(model.processTurn(0, 2));
        assertTrue(model.processTurn(2, 0));
        assertTrue(model.processTurn(1, 2));

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                assertFalse(model.processTurn(row, column));
            }
        }

        assertTrue(model.isDraw());
        assertTrue(model.isFinished());
        assertEquals(1, model.getLastModifiedCellRow());
        assertEquals(2, model.getLastModifiedCellColumn());
    }
}