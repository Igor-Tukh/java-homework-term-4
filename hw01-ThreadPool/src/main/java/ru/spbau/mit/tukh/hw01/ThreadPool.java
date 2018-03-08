package ru.spbau.mit.tukh.hw01;

import java.util.function.Supplier;

/**
 * Interfaces ThreadPool to store supplier-based tasks, synchronized (i.e. multithread) executing this tasks.
 */
public interface ThreadPool {
    /**
     * Finish work of all threads.
     */
    public void shutdown();

    /**
     * Adds task (in a LightFutureImpl object) to the ThreadPool.
     *
     * @param supplier Supplier to build LightFutureImpl.
     * @param <R>      suppliers result type.
     */
    public <R> void addTask(Supplier<R> supplier);

    /**
     * Adds LightFutureImpl to the ThreadPool.
     *
     * @param lightFuture LightFutureImpl object to add.
     * @param <R>         LightFutureImpl result type.
     */
    public <R> void addLightFutureImpl(ThreadPoolImpl.LightFutureImpl<R> lightFuture);
}
