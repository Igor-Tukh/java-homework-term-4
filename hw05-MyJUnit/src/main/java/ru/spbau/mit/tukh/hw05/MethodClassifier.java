package ru.spbau.mit.tukh.hw05;

import ru.spbau.mit.tukh.hw05.annotations.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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

    public List<Method> getAfterMethods() {
        return afterMethods;
    }

    public List<Method> getBeforeMethods() {
        return beforeMethods;
    }

    public List<Method> getAfterClassMethods() {
        return afterClassMethods;
    }

    public List<Method> getBeforeClassMethods() {
        return beforeClassMethods;
    }

    public List<Method> getTestMethods() {
        return testMethods;
    }
}
