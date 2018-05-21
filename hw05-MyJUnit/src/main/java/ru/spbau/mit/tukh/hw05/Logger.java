package ru.spbau.mit.tukh.hw05;

import ru.spbau.mit.tukh.hw05.annotations.Test;

import java.lang.reflect.Method;

public class Logger {
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLUE = "\u001B[34m";

    public enum MethodType {
        BEFORE, AFTER, BEFORE_CLASS, AFTER_CLASS;

        public String getAnnotation() {
            switch (this) {
                case BEFORE:
                    return "@Before";
                case AFTER:
                    return "@After";
                case BEFORE_CLASS:
                    return "@BeforeClass";
                case AFTER_CLASS:
                    return "@AfterClass";
                default:
                    return "@Test";
            }
        }
    }

    private Object instance;
    private int passed;
    private int total;

    Logger(String className, Object instance) {
        this.passed = 0;
        this.total = 0;
        this.instance = instance;
        System.out.println("Testing: class" + className);
    }

    public void invokeMethod(Method method, MethodType methodType) throws InvocationException{
        try {
            method.invoke(instance);
        } catch (Exception e) {
            System.err.println("Error during running " + methodType.getAnnotation() + " method " + method.getName());
            e.printStackTrace();
            throw new InvocationException();
        }
    }

    public void processTestResult(Throwable throwable, Class<? extends Throwable> excpected, long time, String name) {
        boolean failed = (throwable == null && excpected != Test.NothingExpected.class);
        failed |= (throwable != null && excpected != throwable.getClass());

        System.out.print("Test " + name + ": ");
        if (!failed) {
            System.out.println(ANSI_GREEN + "PASSED" + ANSI_RESET);
        } else {
            System.out.println(ANSI_RED + "FAILED" + ANSI_RESET);
            System.out.print(ANSI_RED + "Expected "+ excpected.getName() + " exception, found ");
            System.out.println((throwable == null ? "no exception" : throwable.getClass().getName()) + ANSI_RESET);
        }
        System.out.println("Execution time " + time + " mills.");

        passed += failed ? 0 : 1;
        total++;
    }

    public void printSummary() {
        System.out.println(Utils.repeatString("-", 25));
        System.out.println(ANSI_GREEN + "PASSED:" + passed + ANSI_RESET);
        System.out.println(ANSI_RED+ "FAILED:" + (total - passed) + ANSI_RESET);
    }

    public void printInfo(String info) {
        System.out.println(ANSI_BLUE + info + ANSI_RESET);
    }

    public void printError(String error) {
        System.out.println(ANSI_RED + error + ANSI_RESET);
    }
}
