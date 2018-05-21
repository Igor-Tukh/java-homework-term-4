package ru.spbau.mit.tukh.hw05;

import ru.spbau.mit.tukh.hw05.annotations.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TestLauncher {
    private Class<?> clazz;
    private Logger logger;
    private Object instance;

    TestLauncher(Class<?> clazz) {
        this.clazz = clazz;
        try {
            instance = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            logger.printError("Can't start testing " + clazz.getName());
        }
        this.logger = new Logger(clazz.getName(), instance);
    }

    public void start() {
        MethodClassifier methodClassifier = new MethodClassifier(clazz);

        for (Method beforeClassMethod: methodClassifier.getBeforeClassMethods()) {
            try {
                logger.invokeMethod(beforeClassMethod, Logger.MethodType.BEFORE_CLASS);
            } catch (InvocationException e) {
                logger.printInfo("Testing stopped.");
                return;
            }
        }

        for (Method testMethod: methodClassifier.getTestMethods()) {
            String ignore = testMethod.getAnnotation(Test.class).ignore();
            if (!ignore.equals("")) {
                logger.printInfo("Test " + testMethod.getName() + " skipped cause: " + ignore);
                continue;
            }

            boolean needToSkip = false;

            for (Method beforeMethod: methodClassifier.getBeforeMethods()) {
                try {
                    logger.invokeMethod(beforeMethod, Logger.MethodType.BEFORE);
                } catch (InvocationException e) {
                    logger.printInfo("Test skipped.");
                    needToSkip = true;
                    break;
                }
            }

            if (needToSkip) {
                continue;
            }

            Throwable throwable = null;
            long time = 0;
            try {
                long startTime = System.currentTimeMillis();
                testMethod.invoke(instance);
                time = System.currentTimeMillis() - startTime;
            } catch (InvocationTargetException e) {
                throwable = e.getTargetException();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                continue;
            }

            logger.processTestResult(throwable, testMethod.getAnnotation(Test.class).expected(), time, testMethod.getName());

            for (Method afterMethod: methodClassifier.getAfterMethods()) {
                try {
                    logger.invokeMethod(afterMethod, Logger.MethodType.AFTER_CLASS);
                } catch (InvocationException e) {
                    // Nothing to do here, test have ran.
                }
            }
        }

        logger.printSummary();

        for (Method afterClassMethod: methodClassifier.getAfterClassMethods()) {
            try {
                logger.invokeMethod(afterClassMethod, Logger.MethodType.AFTER_CLASS);
            } catch (InvocationException e) {
                // Nothing to do here, test have ran.
            }
        }
    }
}
