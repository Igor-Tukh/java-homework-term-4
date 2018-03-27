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
     * Abstract ancestor class for all Lazy implementations.
     *
     * @param <T> is generic Lazy argument.
     */
    private static abstract class AbstractLazy<T> implements Lazy<T> {
        private volatile Supplier<T> supplier;
        private T result;

        AbstractLazy(Supplier<T> supplier) {
            this.supplier = supplier;
        }
    }

    /**
     * Implementation of unsynchronized Lazy version.
     *
     * @param <T> is generic Lazy argument.
     */
    private static class SimpleLazy<T> extends AbstractLazy<T> {
        SimpleLazy(Supplier<T> supplier) {
            super(supplier);
        }

        @Override
        public T get() {
            if (super.supplier != null) {
                super.result = super.supplier.get();
                super.supplier = null;
            }
            return super.result;
        }
    }

    private static class MultiLazy<T> extends AbstractLazy<T> {
        MultiLazy(Supplier<T> supplier) {
            super(supplier);
        }

        @Override
        public T get() {
            if (super.supplier != null) {
                synchronized (this) {
                    if (super.supplier != null) {
                        super.result = super.supplier.get();
                        super.supplier = null;
                    }
                }
            }
            return super.result;
        }
    }
}
