package com.account.service.database;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class BasicPoolTest {

    private Pool<TestAutocloseable> pool;

    @Before
    public void setUp() {
        pool = new SimplePool();
    }

    @After
    public void tearDown() throws Exception {
        pool.close();
    }

    @Test
    public void get() {
        TestAutocloseable testAutocloseable = pool.get();
        assertNotNull(testAutocloseable);
        pool.release(testAutocloseable);
    }

    @Test(expected = RuntimeException.class)
    public void getWhenCLose() throws Exception {
        pool.close();
        pool.get();
    }

    @Test
    public void release() {
        TestAutocloseable testAutocloseable = pool.get();
        boolean release = pool.release(testAutocloseable);
        assertTrue(release);
    }
}
