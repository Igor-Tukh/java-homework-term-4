package ru.spbau.mit.tukh.hw01;

import java.util.function.Function;

/**
 * Interfaces LightFuture. Contains supplier (also called task). Is shell-interface for storing supplier
 * in the threading pull.
 *
 * @param <R>
 */
public interface LightFuture<R> {
    /**
     * Checks if task finished (i.e. result of supplier calculated).
     *
     * @return true if task is completed and false otherwise.
     */
    public boolean isReady();

    /**
     * Calculates result of executing (applying) supplier. If task isn't complete yet, waits until it will finish.
     *
     * @return result of executing supplier.
     * @throws LightExecutionException
     */
    public R get() throws LightExecutionException;

    /**
     * Creates new LightFuture object, which supplier is result of composition this supplier and argument function.
     * Then puts it in to the threading pull.
     *
     * @param function function to compose.
     * @param <N>      generic type of function result type.
     * @return new LightFuture object, which supplier is result of composition this supplier and argument function.
     */
    public <N> LightFuture<N> thenApply(Function<R, N> function);
}
