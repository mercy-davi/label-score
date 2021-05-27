package com.example.labelscore.service;

/**
 * @Description TODO
 * @Date 2021/4/4 22:12
 * @Created by hdw
 */
public class InputInCompleteException extends RuntimeException {
    public InputInCompleteException(String message) {
        super(message);
    }

    public InputInCompleteException(String message, Throwable cause) {
        super(message, cause);
    }

    public InputInCompleteException(Throwable cause) {
        super(cause);
    }
}
