package com.example.labelscore.util;

/**
 * @Description TODO
 * @Date 2021/4/3 22:21
 * @Created by hdw
 */
public class DoubleRange extends Range {
    private double min;
    private double max;

    public DoubleRange(double min, double max, int openClosedFlag) {
        super(openClosedFlag);
        this.min = min;
        this.max = max;
    }

    public DoubleRange(double min, double max, boolean leftOpen, boolean rightOpen) {
        this(min, max, convert2OpenClosedFlag(leftOpen, rightOpen));
    }

    @Override
    public boolean contains(int number) {
        return contains((double) number);
    }

    @Override
    public boolean contains(double number) {
        switch (openClosedFlag) {
            case OPEN_OPEN:
                return min < number && number < max;
            case OPEN_CLOSED:
                return min < number && number <= max;
            case CLOSED_OPEN:
                return min <= number && number < max;
            case CLOSED_CLOSED:
                return min <= number && number <= max;
            default:
        }
        return false;
    }
}
