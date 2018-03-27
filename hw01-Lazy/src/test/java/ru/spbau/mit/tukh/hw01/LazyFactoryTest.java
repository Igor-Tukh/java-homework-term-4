package ru.spbau.mit.tukh.hw01;

import org.junit.Test;

import java.util.function.Supplier;

import static org.junit.Assert.*;

public class LazyFactoryTest {
    private static final int THREADS_COUNT = 17;
    private SupplierWithGetCallCount<Boolean> testSupplier;
    private Lazy<Boolean> lazy;

    private void initSimpleLazy() {
        testSupplier = new SupplierWithGetCallCount<>(() -> false);
        lazy = LazyFactory.createLazy(testSupplier);
    }

    private void initMultiLazy() {
        testSupplier = new SupplierWithGetCallCount<>(() -> false);
        lazy = LazyFactory.createMultiLazy(testSupplier);
    }

    private void initNullSupplier() {
        testSupplier = new SupplierWithGetCallCount<>(() -> null);
        lazy = LazyFactory.createMultiLazy(testSupplier);
    }

    @Test
    public void testUnsynchronizedLazyGetCalledOnce() {
        initSimpleLazy();
        for (int i = 0; i < 100; i++) {
            assertFalse(lazy.get());
        }
        assertTrue(testSupplier.calledOnce());
    }

    @Test
    public void testSynchronizedLazy() throws InterruptedException {
        initMultiLazy();
        Thread[] threads = new Thread[THREADS_COUNT];
        Runnable runnable = () -> {
            for (int i = 0; i < 10; i++) lazy.get();
        };

        for (int i = 0; i < THREADS_COUNT; i++) {
            threads[i] = new Thread(runnable);
            threads[i].start();
        }

        for (int i = 0; i < THREADS_COUNT; i++) {
            threads[i].join();
        }

        assertTrue(testSupplier.calledOnce());
    }

    @Test
    public void testSynchronizedLazyNullSupplier() throws InterruptedException {
        initNullSupplier();
        Thread[] threads = new Thread[THREADS_COUNT];
        Runnable runnable = () -> {
            for (int i = 0; i < 100000; i++) assertNull(lazy.get());
        };

        for (int i = 0; i < THREADS_COUNT; i++) {
            threads[i] = new Thread(runnable);
            threads[i].start();
        }

        for (int i = 0; i < THREADS_COUNT; i++) {
            threads[i].join();
        }

        assertTrue(testSupplier.calledOnce());
    }


    /**
     * Supplier shell-class. Counts number of calls.
     *
     * @param <T> is generic argument type.
     */
    private class SupplierWithGetCallCount<T> implements Supplier<T> {
        private Supplier<T> supplier;
        private int callsCount;

        SupplierWithGetCallCount(Supplier<T> supplier) {
            this.supplier = supplier;
        }

        @Override
        public T get() {
            callsCount++;
            return supplier.get();
        }

        /**
         * Checks if get method was called at most once.
         *
         * @return true if get was called once or zero times and false otherwise.
         */

        boolean calledOnce() {
            return callsCount == 1;
        }
    }
}