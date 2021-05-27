package com.example.labelscore.service;

import com.example.labelscore.bean.TagRuleDef;

import static com.example.labelscore.core.ScoreRuleType.ManualInput;

/**
 * @Description
 * 目前只有从标签 定量系统给分，定性人工给，未来需要考虑权重计算主次标签分
 * @Date 2021/4/4 22:17
 * @Created by hdw
 */
public class PEDefaultValueScoreStrategy extends AbstractValueScoreStrategy {
    @Override
    public boolean needValue(TagRuleDef tagRuleDef) {
        return tagRuleDef.level() == 4;
    }

    @Override
    public boolean isAutoValue(TagRuleDef tagRuleDef) {
        return true;
    }

    @Override
    public boolean needAutoScore(TagRuleDef tagRuleDef) {
        return null != tagRuleDef.getScoreType() && tagRuleDef.getScoreType() != ManualInput;
    }

    @Override
    public boolean isSumByWeight(TagRuleDef tagRuleDef) {
        return !needValue(tagRuleDef);
    }

    @Override
    public boolean canAdjustScore(TagRuleDef tagRuleDef) {
        return tagRuleDef.level() == 3 || tagRuleDef.level() == 4;
    }

    @Override
    public boolean fe_scoreVisible(TagRuleDef tagRuleDef) {
        return true;
    }
}
