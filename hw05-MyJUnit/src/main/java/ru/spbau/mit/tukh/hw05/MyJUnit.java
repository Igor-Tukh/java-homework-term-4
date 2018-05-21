package ru.spbau.mit.tukh.hw05;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class MyJUnit {
    private static final String HELP_STRING = "Usage: <path to file with tests> as first argument\n<filename as second>";

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Incorrect number of arguments");
            System.err.println(HELP_STRING);
            System.exit(0);
        }

        Class<?> clazz = lookupClass(args[0], args[1]);
        if (clazz != null) {
            TestLauncher testLauncher = new TestLauncher(clazz);
            testLauncher.start();
        }
    }

    private static Class<?> lookupClass(String path, String name) {
        try {
            URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{new URL("file:" + path)});
            return urlClassLoader.loadClass(name);
        } catch (ClassNotFoundException | MalformedURLException e) {
            System.err.println("Error loading class.");
            System.err.println(HELP_STRING);
            e.printStackTrace();
            return null;
        }
    }
}
