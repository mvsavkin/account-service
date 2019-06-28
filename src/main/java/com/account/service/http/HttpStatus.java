package com.account.service.http;

public enum  HttpStatus {
    OK(200, "OK"),

    BAD_REQUEST(400, "Bad Request"),

    UNAUTHORIZED(401, "Unauthorized"),

    NOT_FOUND(406, "Not Found"),
    NOT_ACCEPTABLE(406, "Not Acceptable"),

    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),

    INSUFFICIENT_MONEY(600, "Insufficient funds"),
    ACCOUNT_NOT_FOUND(601, "Account not found");

    private final int value;

    private final String reasonPhrase;

    HttpStatus(int value, String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }


    /**
     * Return the integer value of this status code.
     */
    public int value() {
        return this.value;
    }

    /**
     * Return the reason phrase of this status code.
     */
    public String getReasonPhrase() {
        return this.reasonPhrase;
    }
}
