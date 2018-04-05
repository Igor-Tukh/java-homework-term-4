package ru.spbau.mit.tukh.hw01;

import java.util.function.Supplier;

/**
 * Lazy factory class. Produces SimpleLazy and MultiLazy classes for unsynchronized and synchronized Lazy
 * implementations respectively.
 */
public class LazyFactory {
    /**
     * Creates SimpleLazy (unsynchronized) implementation of Lazy interface.
     *
     * @param supplier is supplier to apply.
     * @param <T>      is supplier generic argument.
     * @return new SimpleLazy Object.
     */
    public static <T> Lazy<T> createLazy(Supplier<T> supplier) {
        return new SimpleLazy<>(supplier);
    }

    /**
     * Creates MultiLazy (synchronized) implementation of Lazy interface.
     *
     * @param supplier is supplier to apply.
     * @param <T>      is supplier generic argument.
     * @return new MultiLazy Object.
     */
    public static <T> Lazy<T> createMultiLazy(Supplier<T> supplier) {
        return new MultiLazy<>(supplier);
    }

    /**
     * Implementation of unsynchronized Lazy version.
     *
     * @param <T> is generic Lazy argument.
     */
    private static class SimpleLazy<T> implements Lazy<T> {
        private Supplier<T> supplier;
        private T result;

        SimpleLazy(Supplier<T> supplier) {
            this.supplier = supplier;
        }

        @Override
        public T get() {
            if (supplier != null) {
                result = supplier.get();
                supplier = null;
            }
            return result;
        }
    }

    /**
     * Implementation of synchronized Lazy version.
     *
     * @param <T> is generic Lazy argument.
     */
    private static class MultiLazy<T> implements Lazy<T> {
        private volatile Supplier<T> supplier;
        private T result;

        MultiLazy(Supplier<T> supplier) {
            this.supplier = supplier;
        }

        @Override
        public T get() {
            if (supplier != null) {
                synchronized (this) {
                    if (supplier != null) {
                        result = supplier.get();
                        supplier = null;
                    }
                }
            }
            return result;
        }
    }
}
