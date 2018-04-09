package ru.spbau.mit.tukh.hw01;

/**
 * Interfaces, which represent lazy calculation for supplier.
 *
 * @param <T> is generic argument type.
 */
public interface Lazy<T> {
    /**
     * lazy calculation. Supplier method get will be used at most one time.
     *
     * @return result of calculation.
     */
    T get();
}