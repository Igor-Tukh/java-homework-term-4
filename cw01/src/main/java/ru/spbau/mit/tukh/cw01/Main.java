package ru.spbau.mit.tukh.cw01;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        Path path = Paths.get("src/main/resources");
        long currentTime = System.currentTimeMillis();
        try {
            byte[] ansRecursive = RecursiveSolver.getMD5(path);
            long recursiveSolverTime = System.currentTimeMillis() - currentTime;
            currentTime = System.currentTimeMillis();
            byte[] ansForkJoin = RecursiveSolver.getMD5(path);
            long ForkJoinSolverTime = System.currentTimeMillis() - currentTime;
            System.err.println("Simple recursive solver worked " + ForkJoinSolverTime +
                    " mills.\nForkJoinSolver worked " + recursiveSolverTime + " mills");

            boolean arraysAreEquals = ansForkJoin.length == ansRecursive.length;
            if (arraysAreEquals) {
                for (int i = 0; i < ansForkJoin.length; i++) {
                    arraysAreEquals &= (ansForkJoin[i] == ansRecursive[i]);
                }
            }
            System.err.println("Answers are equal: " + arraysAreEquals + ".");

        } catch (IOException e) {
            //Nothing to do
        }
    }
}
