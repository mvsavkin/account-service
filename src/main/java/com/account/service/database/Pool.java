package com.account.service.database;

/**
 * Pool.
 */
public interface Pool<T> extends AutoCloseable {

    /**
     * Return object from pool.
     * @return object from pool.
     */
    T get();


    /**
     * Return object to pool.
     * @param object instance Pool.
     * @return true if success.
     */
    boolean release(T object);
}
