package com.example.score.bean;

/**
 * @Classname TagReadWriteState
 * @Description TODO
 * @Date 2021/3/31
 * @Author hdw
 */
public class TagReadWriteState {
    /**
     * value exist
     */
    public static final int VALUE_E = 1;
    /**
     * value writable
     */
    public static final int VALUE_W = 1 << 1;
    /**
     * score visible
     */
    public static final int SCORE_V = 1 << 2;
    /**
     * score writable
     */
    public static final int SCORE_W = 1 << 3;
    /**
     * score adjustable
     */
    public static final int SCORE_A = 1 << 4;

    private int state;

    public TagReadWriteState() {

    }

    public TagReadWriteState(int state) {
        this.state = state;
    }

    public boolean f_value_e() {
        return (state & VALUE_E) != 0;
    }

    public boolean f_value_w() {
        return (state & VALUE_W) != 0;
    }

    public boolean f_score_v() {
        return (state & SCORE_V) != 0;
    }

    public boolean f_score_w() {
        return (state & SCORE_W) != 0;
    }

    public boolean f_score_a() {
        return (state & SCORE_A) != 0;
    }

    public int f_state() {
        return state;
    }
}
