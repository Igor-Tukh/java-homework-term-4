package ru.spbau.mit.tukh.hw05;

/**
 * Class for utils.
 */
public class Utils {
    /**
     * Repeats string several times.
     * @param string is string to repeat.
     * @param repeatCount is number of the repeats.
     * @return repeated string.
     */
    public static String repeatString(String string, int repeatCount) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < repeatCount; i++) {
            stringBuilder.append(string);
        }
        return stringBuilder.toString();
    }
}
