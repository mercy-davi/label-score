package com.example.labelscore.service;

import com.example.labelscore.bean.TagRuleDef;

import static com.example.labelscore.service.ScoreTreeLevel.TERTIARY_TAG;

/**
 * @Description TODO
 * @Date 2021/4/4 22:11
 * @Created by hdw
 */
public class FPMemberValueScoreStrategy extends AbstractValueScoreStrategy {
    @Override
    public boolean needValue(TagRuleDef tagRuleDef) {
        return false;
    }

    // consider auto in future
    @Override
    public boolean isAutoValue(TagRuleDef tagRuleDef) {
        return false;
    }

    @Override
    public boolean needAutoScore(TagRuleDef tagRuleDef) {
        return false;
    }

    @Override
    public boolean isSumByWeight(TagRuleDef tagRuleDef) {
        return false;
    }

    @Override
    public boolean canAdjustScore(TagRuleDef tagRuleDef) {
        return false;
    }

    @Override
    public boolean fe_scoreVisible(TagRuleDef tagRuleDef) {
        return tagRuleDef.level() != TERTIARY_TAG;
    }
}
