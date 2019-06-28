package com.account.service.exceptions;

public class InternalRepositoryException extends RuntimeException {

    public InternalRepositoryException() {
    }

    public InternalRepositoryException(String s) {
        super(s);
    }

    public InternalRepositoryException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public InternalRepositoryException(Throwable throwable) {
        super(throwable);
    }

    public InternalRepositoryException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
