package ru.spbau.mit.tukh.cw01;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class ForkJoinSolverTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testGetMD5EmptyDirectory() {
        Path path = Paths.get("src/test/resources/Empty");
        assertArrayEquals(Utils.getTestEmptyDirectoryMD5(), ForkJoinSolver.getMD5(path));
    }

    @Test
    public void testGetMD5NotEmptyDirectory() {
        Path path = Paths.get("src/test/resources/NotEmpty");
        assertArrayEquals(Utils.getTestNotEmptyDirectoryMD5(), ForkJoinSolver.getMD5(path));
    }
}