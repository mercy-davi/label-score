package com.example.labelscore.service;

import com.example.labelscore.bean.TagRuleDef;
import com.example.labelscore.core.ValueScoreStrategy;

import static com.example.labelscore.bean.TagReadWriteState.SCORE_A;
import static com.example.labelscore.bean.TagReadWriteState.SCORE_V;
import static com.example.labelscore.bean.TagReadWriteState.SCORE_W;
import static com.example.labelscore.bean.TagReadWriteState.VALUE_E;
import static com.example.labelscore.bean.TagReadWriteState.VALUE_W;

/**
 * @Description TODO
 * @Date 2021/4/4 20:30
 * @Created by hdw
 */
public abstract class AbstractValueScoreStrategy implements ValueScoreStrategy {
    @Override
    public int fe_state(TagRuleDef tagRuleDef) {
        int state = 0;
        boolean needV = needValue(tagRuleDef);
        if (needV) {
            state |= VALUE_E;
        }
        if (needV) {
            state |= VALUE_W;
        }
        if (fe_scoreVisible(tagRuleDef)) {
            state |= SCORE_V;
        }
        if (!needAutoScore(tagRuleDef)) {
            state |= SCORE_W;
        }
        if (canAdjustScore(tagRuleDef)) {
            state |= SCORE_A;
        }
        return state;
    }
}
