package com.example.score.core;

/**
 * @Description TODO
 * @Date 2021/4/4 15:50
 * @Created by hdw
 */
public class RuleExprInvalidException extends RuntimeException {
    public RuleExprInvalidException(String message) {
        super(message);
    }

    public RuleExprInvalidException(String message, Throwable cause) {
        super(message, cause);
    }

    public RuleExprInvalidException(Throwable cause) {
        super(cause);
    }

    public RuleExprInvalidException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
