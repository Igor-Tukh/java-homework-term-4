package ru.spbau.mit.tukh.hw05;

public class Utils {
    public static String repeatString(String string, int repeatCount) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < repeatCount; i++) {
            stringBuilder.append(string);
        }
        return stringBuilder.toString();
    }
}
