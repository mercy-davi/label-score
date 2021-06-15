package com.example.score.core;

/**
 * @Description TODO
 * @Date 2021/4/3 11:24
 * @Created by hdw
 */
public interface ScoreCalculator {

    /**
     * calculate both value and score if possible
     * @param ruleContext
     * @return
     */
    Double calc(RuleContext ruleContext);
}
