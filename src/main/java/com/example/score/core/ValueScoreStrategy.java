package com.example.score.core;

import com.example.score.bean.TagRuleDef;

/**
 * @Description TODO
 * @Date 2021/4/4 17:28
 * @Created by hdw
 */
public interface ValueScoreStrategy {
    String DEFAULT_NAME = "defaultValueScoreStrategy";
    ThreadLocal<ValueScoreStrategy> STRATEGY = new ThreadLocal<>();
    ThreadLocal<String> CURRENT_USER = new ThreadLocal<>();

    boolean needValue(TagRuleDef tagRuleDef); // A check
    boolean isAutoValue(TagRuleDef tagRuleDef); // b check, after a check
    boolean needAutoScore(TagRuleDef tagRuleDef); // c check
    boolean isSumByWeight(TagRuleDef tagRuleDef); // d check, after c check
    boolean canAdjustScore(TagRuleDef tagRuleDef); // e check, independent check

    boolean fe_scoreVisible(TagRuleDef tagRuleDef);
    int fe_state(TagRuleDef tagRuleDef);
}
