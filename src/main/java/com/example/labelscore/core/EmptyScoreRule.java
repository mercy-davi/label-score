package com.example.labelscore.core;

/**
 * @Description TODO
 * @Date 2021/4/3 20:55
 * @Created by hdw
 */
public class EmptyScoreRule implements ScoreRule {

    public static final EmptyScoreRule INSTANCE = new EmptyScoreRule();

    @Override
    public Double calc(RuleContext ruleContext) {
        return null;
    }

    @Override
    public ScoreRuleType scoreRuleType() {
        return null;
    }

    @Override
    public double exclusiveMatchScore() {
        return -1;
    }
}
