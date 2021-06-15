package com.example.score.service;

/**
 * @Classname ScoreFeatures
 * @Description TODO
 * @Date 2021/3/31
 * @Author hdw
 */
public class ScoreFeatures {
    private int features;

    public ScoreFeatures () {
        this.features = collectDefaultStates();
    }

    public ScoreFeatures (int features) {
        this.features = features;
    }

    public ScoreFeatures enable(ScoreFeature feature) {
        features |= feature.mask();
        return this;
    }

    public ScoreFeatures disable(ScoreFeature feature) {
        features &= ~feature.mask();
        return this;
    }

    public boolean isEnable(ScoreFeature feature) {
        return (features & feature.mask()) != 0;
    }

    public static int collectDefaultStates() {
        int defaultState = 0;
        for (ScoreFeature feature : ScoreFeature.values()) {
            if (feature.defaultState()) {
                defaultState |= feature.mask();
            }
        }
        return defaultState;
    }
}
