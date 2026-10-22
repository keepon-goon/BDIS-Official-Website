package com.bdis.bdis_website.exception;

public class ResumeException extends RuntimeException {
    private final int status;

    public ResumeException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
