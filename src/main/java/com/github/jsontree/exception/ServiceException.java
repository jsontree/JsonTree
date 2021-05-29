package com.github.jsontree.exception;

public class ServiceException extends Exception {

    private static final long serialVersionUID = 1L;

    private final String error;

    public ServiceException(String error) {
        super(error);
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
