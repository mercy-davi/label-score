package com.example.labelscore.core;

/**
 * @Description TODO
 * @Date 2021/4/4 15:45
 * @Created by hdw
 */
public class RuleEvalException extends RuntimeException {
    public RuleEvalException(String message) {
        super(message);
    }

    public RuleEvalException(String message, Throwable cause) {
        super(message, cause);
    }

    public RuleEvalException(Throwable cause) {
        super(cause);
    }

    public RuleEvalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
