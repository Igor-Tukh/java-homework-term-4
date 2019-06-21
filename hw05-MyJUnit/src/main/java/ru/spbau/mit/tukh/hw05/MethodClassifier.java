package ru.spbau.mit.tukh.hw05;

import ru.spbau.mit.tukh.hw05.annotations.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for classify methods of a class by MyJUnits annotations.
 */
public class MethodClassifier {
    private ArrayList<Method> afterMethods = new ArrayList<>();
    private ArrayList<Method> beforeMethods = new ArrayList<>();
    private ArrayList<Method> afterClassMethods = new ArrayList<>();
    private ArrayList<Method> beforeClassMethods = new ArrayList<>();
    private ArrayList<Method> testMethods = new ArrayList<>();

    MethodClassifier(Class<?> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            method.setAccessible(true);
            if (method.isAnnotationPresent(After.class)) {
                afterMethods.add(method);
            }
            if (method.isAnnotationPresent(Before.class)) {
                beforeMethods.add(method);
            }
            if (method.isAnnotationPresent(AfterClass.class)) {
                afterClassMethods.add(method);
            }
            if (method.isAnnotationPresent(BeforeClass.class)) {
                beforeClassMethods.add(method);
            }
            if (method.isAnnotationPresent(Test.class)) {
                testMethods.add(method);
            }
        }
    }

    /**
     * Finds methods annotated as after.
     * @return class After methods.
     */
    public List<Method> getAfterMethods() {
        return afterMethods;
    }

    /**
     * Finds methods annotated as before.
     * @return class Before methods.
     */
    public List<Method> getBeforeMethods() {
        return beforeMethods;
    }

    /**
     * Finds methods annotated as after class.
     * @return class AfterClass methods.
     */
    public List<Method> getAfterClassMethods() {
        return afterClassMethods;
    }

    /**
     * Finds methods annotated as before class.
     * @return class BeforeClass methods.
     */
    public List<Method> getBeforeClassMethods() {
        return beforeClassMethods;
    }

    /**
     * Finds methods annotated as test.
     * @return class Test methods.
     */
    public List<Method> getTestMethods() {
        return testMethods;
    }
}
