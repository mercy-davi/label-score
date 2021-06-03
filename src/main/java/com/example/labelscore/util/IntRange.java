package com.example.labelscore.util;

/**
 * @Classname IntRange
 * @Description TODO
 * @Date 2021/3/31
 * @Author hdw
 */
public class IntRange extends Range{
    private int min;
    private int max;

    public IntRange(int min, int max, boolean leftOpen, boolean rightOpen) {
        this(min, max, convert2OpenClosedFlag(leftOpen, rightOpen));
    }

    public IntRange(int min, int max, int openClosed) {
        super(openClosed);
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean contains(int number) {
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

    @Override
    public boolean contains(double number) {
        int num = (int) number;
        if (num == number) {
            return contains(num);
        } else if (num < number) {
            return num < max && num >= min;
        } else {
            return num <= max && num > min;
        }
    }
}
