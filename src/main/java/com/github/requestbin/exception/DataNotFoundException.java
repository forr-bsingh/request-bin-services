package com.github.requestbin.exception;

public class DataNotFoundException extends Exception {

    private static final long serialVersionUID = -3882665267210824276L;

    public DataNotFoundException(String message) {
        super(message);
    }

    public DataNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
