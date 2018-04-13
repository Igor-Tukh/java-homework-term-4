package ru.spbau.mit.tukh.cw01;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class RecursiveSolverTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testGetMD5EmptyDirectory() {
        Path path = Paths.get("src/test/resources/Empty");
        try {
            assertArrayEquals(Utils.getTestEmptyDirectoryMD5(), RecursiveSolver.getMD5(path));
        } catch (IOException e) {
            // There is rule
        }
    }

    @Test
    public void testGetMD5NotEmptyDirectory() {
        Path path = Paths.get("src/test/resources/NotEmpty");
        try {
            assertArrayEquals(Utils.getTestNotEmptyDirectoryMD5(), RecursiveSolver.getMD5(path));
        } catch (IOException e) {
            // There is rule
        }
    }
}