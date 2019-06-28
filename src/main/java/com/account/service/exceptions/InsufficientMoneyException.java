package com.account.service.exceptions;

public class InsufficientMoneyException extends RuntimeException {

    public InsufficientMoneyException() {
    }

    public InsufficientMoneyException(String message) {
        super(message);
    }

    public InsufficientMoneyException(String message, Throwable cause) {
        super(message, cause);
    }

    public InsufficientMoneyException(Throwable cause) {
        super(cause);
    }

    public InsufficientMoneyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
