package com.account.service.database;

import java.util.ArrayList;

public class SimplePool extends BasicPool<TestAutocloseable> {

    public SimplePool() {
        this.connectionPool = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            connectionPool.add(new TestAutocloseable());
        }
    }
}
