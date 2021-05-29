package com.example.labelscore.service;

/**
 * @Classname ScoreFeature
 * @Description TODO
 * @Date 2021/3/31
 * @Author hdw
 */
public enum ScoreFeature {

    /**
     * whether need biz model, PE requires and FP not
     */
    BizModelRequired(false),

    ConditionalTagExist(false);

    private final boolean defaultState;

    private final int mask;

    ScoreFeature(boolean defaultState) {
        this.defaultState = defaultState;
        this.mask = 1 << ordinal();
    }

    public boolean defaultState() {
        return defaultState;
    }

    public int mask() {
        return mask;
    }
}
