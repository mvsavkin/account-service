package com.account.service.database;

public class TestAutocloseable implements AutoCloseable {
    @Override
    public void close() throws Exception {
        System.out.println("Do nothing...");
    }
}
