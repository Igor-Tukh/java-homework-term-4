package ru.spbau.mit.tukh.hw05.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that method is a test method.
 */
@Target(value = ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Test {
    /**
     * No exceptions expected.
     */
    class NothingExpected extends Throwable {}

    /**
     * Expected exception in the method, default value is NothingExpected.class.
     * @return expected exception class.
     */
    Class<? extends Throwable> expected() default NothingExpected.class;

    /**
     * Method ignore reason, default value is "".
     * @return method ignore reason
     */
    String ignore() default "";
}