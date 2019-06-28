package com.account.service.database;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class BasicPool<T extends AutoCloseable> implements Pool<T> {

    private static final int CONDITION_AWAIT_TIMEOUT_SC = 10;
    private static final int LATCH_AWAIT_TIMEOUT_SC = 30;

    private volatile boolean isCondition;
    private volatile boolean isClosed;
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    private CountDownLatch latch;

    private List<T> usedConnections = new ArrayList<>();
    protected List<T> connectionPool;

    @Override
    public T get() {
        lock.lock();
        try {
            if (isClosed) {
                throw new RuntimeException("Pool is closed");
            }
            if (connectionPool.isEmpty()) {
                isCondition = true;
                condition.await(CONDITION_AWAIT_TIMEOUT_SC, TimeUnit.SECONDS);
            }
            T connection = connectionPool.remove(connectionPool.size() - 1);
            usedConnections.add(connection);
            return connection;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean release(T object) {
        lock.lock();
        try {
            connectionPool.add(object);
            boolean result = usedConnections.remove(object);

            if (isClosed) {
                if (latch != null && latch.getCount() != 0) {
                    latch.countDown();
                }
            }

            if (isCondition) {
                condition.signal();
                isCondition = false;
            }
            return result;
        } finally {
            lock.unlock();
        }
    }


    @Override
    public void close() throws InterruptedException {
        lock.lock();
        if (!isClosed) {
            isClosed = true;
            if (!usedConnections.isEmpty()) {
                latch = new CountDownLatch(usedConnections.size());
                lock.unlock();
                System.out.println("Start for waiting release object from pool");
                latch.await(LATCH_AWAIT_TIMEOUT_SC, TimeUnit.SECONDS);
                System.out.println("Unblocking");
            } else {
                lock.unlock();
            }
            for (AutoCloseable connection : connectionPool) {
                try {
                    connection.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("Pool has already closed.");
            lock.unlock();
        }


    }
}
