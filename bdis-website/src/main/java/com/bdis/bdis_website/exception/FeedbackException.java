package com.bdis.bdis_website.exception;

public class FeedbackException extends RuntimeException {
    private final int status;

    public FeedbackException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() { return status; }
}
