package ru.spbau.mit.tukh.hw01;

/**
 * Class for exceptions which can arise during executing LightFuture objects get method.
 */
public class LightExecutionException extends Exception {
    public LightExecutionException(String message) {
        super(message);
    }
}
