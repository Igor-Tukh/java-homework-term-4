package ru.spbau.mit.tukh.hw01;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ru.spbau.mit.tukh.hw01.ThreadPoolImpl.LightFutureImpl;

import java.util.ArrayList;
import java.util.function.Supplier;

import static org.junit.Assert.*;

public class ThreadPoolImplTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private ThreadPoolImpl threadPool;
    private Supplier<Integer> testSupplier;
    private volatile int counter;

    private void init(int threadsNumber) {
        threadPool = new ThreadPoolImpl(threadsNumber);
    }

    private void initCounting(int threadsNumber) {
        init(threadsNumber);
        testSupplier = new TestCountingSupplier();
    }

    private void initSleeping(int threadsNumber) {
        init(threadsNumber);
        testSupplier = new TestSleepingSupplier();
    }

    /**
     * Simple test with one thread. Tests multiple calling get method.
     */
    @Test
    public void testSingleTask() throws LightExecutionException {
        initCounting(1);
        LightFutureImpl<Integer> lightFuture = threadPool.new LightFutureImpl<>(testSupplier);
        threadPool.addLightFutureImpl(lightFuture);
        assertEquals(Integer.valueOf(0), lightFuture.get());
        assertTrue(lightFuture.isReady());
        assertEquals(Integer.valueOf(0), lightFuture.get());
    }

    /**
     * Checks if there are at least 4 threads in TP indeed.
     */
    @Test
    public void testThreadsCnt() {
        init(4);
        for (int i = 0; i < 4; i++) {
            LightFutureImpl<Integer> lightFuture = threadPool.new LightFutureImpl<>(new CountingStopSupplier());
            threadPool.addLightFutureImpl(lightFuture); // Each kills one thread but also increments counter
        }
        threadPool.shutdown();
        assertEquals(4, counter);
    }

    /**
     * Tests isReadyMethod with waiting part;
     */
    @Test
    public void testIsReady() {
        initSleeping(2);
        LightFutureImpl<Integer> lightFuture1 = threadPool.new LightFutureImpl<>(testSupplier);
        LightFutureImpl<Integer> lightFuture2 = threadPool.new LightFutureImpl<>(new TestSupplier());
        assertFalse(lightFuture1.isReady() || lightFuture2.isReady());
        threadPool.addLightFutureImpl(lightFuture1);
        threadPool.addLightFutureImpl(lightFuture2);
        threadPool.shutdown();
        assertTrue(lightFuture1.isReady() && lightFuture2.isReady());
    }

    /**
     * Tests get method in LightFuture when threads number is less than tasks number.
     */
    @Test
    public void testGetWithoutException() {
        initSleeping(1);
        LightFutureImpl<Integer> lightFuture1 = threadPool.new LightFutureImpl<>(testSupplier);
        LightFutureImpl<Integer> lightFuture2 = threadPool.new LightFutureImpl<>(testSupplier);
        LightFutureImpl<Integer> lightFuture3 = threadPool.new LightFutureImpl<>(testSupplier); // 3 > 1
        assertFalse(lightFuture1.isReady() || lightFuture2.isReady() || lightFuture3.isReady());
        threadPool.addLightFutureImpl(lightFuture1);
        threadPool.addLightFutureImpl(lightFuture2);
        threadPool.addLightFutureImpl(lightFuture3);
        threadPool.shutdown();
        assertTrue(lightFuture1.isReady() && lightFuture2.isReady() && lightFuture3.isReady());
    }

    /**
     * Checks LightExecutionException throws (Division by zero in supplier).
     */
    @Test
    public void testGetWithException() throws LightExecutionException {
        init(1);
        LightFutureImpl<Integer> lightFuture = threadPool.new LightFutureImpl<>(new ExceptionSupplier());
        threadPool.addLightFutureImpl(lightFuture);
        thrown.expect(LightExecutionException.class);
        lightFuture.get();
        threadPool.shutdown();
    }

    @Test
    public void testCheckShutdown() {
        init(3);
        ArrayList<LightFutureImpl> lightFutureArrayList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            LightFutureImpl<Integer> lightFuture = threadPool.new LightFutureImpl<>(new TestSupplier());
            lightFutureArrayList.add(lightFuture);
            threadPool.addLightFutureImpl(lightFuture);
        }

        threadPool.shutdown();
        for (LightFutureImpl lightFuture : lightFutureArrayList) {
            assertTrue(lightFuture.isReady());
        }
    }

    @Test
    public void testThenApply() throws LightExecutionException {
        initCounting(3);
        LightFutureImpl<Integer> lightFuture0 = threadPool.new LightFutureImpl<>(new TestSupplier());
        threadPool.addLightFutureImpl(lightFuture0);
        LightFuture<Integer> lightFuture1 = lightFuture0.thenApply(integer -> integer - 27);
        LightFuture<Integer> lightFuture2 = lightFuture0.thenApply(integer -> integer - 25);
        LightFuture<Boolean> lightFuture3 = lightFuture2.thenApply(integer -> integer == 5 * (-5));
        threadPool.shutdown();

        assertTrue(lightFuture0.isReady());
        assertTrue(lightFuture1.isReady());
        assertTrue(lightFuture2.isReady());
        assertTrue(lightFuture3.isReady());

        assertEquals(Integer.valueOf(0), lightFuture0.get());
        assertEquals(Integer.valueOf(-27), lightFuture1.get());
        assertEquals(Integer.valueOf(-25), lightFuture2.get());
        assertEquals(true, lightFuture3.get());
    }

    private class TestCountingSupplier implements Supplier<Integer> {
        private int count = 0;

        @Override
        public Integer get() {
            return count++;
        }
    }

    private class TestSleepingSupplier implements Supplier<Integer> {
        @Override
        public Integer get() {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 0;
        }
    }

    private class TestSupplier implements Supplier<Integer> {
        @Override
        public Integer get() {
            return 0;
        }
    }

    private class CountingStopSupplier extends ThreadPoolImpl.StopSupplier {
        @Override
        public Integer get() {
            synchronized (ThreadPoolImplTest.this) {
                counter++;
            }
            return 0;
        }
    }

    private class ExceptionSupplier implements Supplier<Integer> {
        @Override
        public Integer get() {
            int a = 0;
            int b = 0;
            return a / b;
        }
    }
}