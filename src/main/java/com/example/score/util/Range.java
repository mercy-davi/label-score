package com.example.score.util;

/**
 * @Classname Range
 * @Description TODO
 * @Date 2021/3/31
 * @Author hdw
 */
public abstract class Range {
    protected final int openClosedFlag;

    protected Range(int openClosedFlag) {
        this.openClosedFlag = openClosedFlag;
    }

    public abstract boolean contains(int number);

    public abstract boolean contains(double number);

    public static final int OPEN_OPEN = 0;
    public static final int OPEN_CLOSED = 1;
    public static final int CLOSED_OPEN = 2;
    public static final int CLOSED_CLOSED = 3;

    static int convert2OpenClosedFlag(boolean leftOpen, boolean rightOpen) {
        if (leftOpen && rightOpen) {
            return OPEN_OPEN;
        }
        if (leftOpen) {
            return OPEN_CLOSED;
        }
        if (rightOpen) {
            return CLOSED_OPEN;
        }
        return CLOSED_CLOSED;
    }
}
