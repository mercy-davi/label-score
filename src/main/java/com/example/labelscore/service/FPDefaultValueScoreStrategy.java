package com.example.labelscore.service;

import com.example.labelscore.bean.TagRuleDef;
import com.example.labelscore.core.MultiInputValueSourceExp;
import com.example.labelscore.core.ValueSourceExp;
import org.springframework.util.ObjectUtils;

import static com.example.labelscore.service.ScoreTreeLevel.TERTIARY_TAG;

/**
 * @Description TODO
 * @Date 2021/4/4 22:03
 * @Created by hdw
 */
public class FPDefaultValueScoreStrategy extends AbstractValueScoreStrategy {

    @Override
    public boolean needValue(TagRuleDef tagRuleDef) {
        return tagRuleDef.level() == 4;
    }

    // consider auto in future
    @Override
    public boolean isAutoValue(TagRuleDef tagRuleDef) {
        if (ObjectUtils.isEmpty(tagRuleDef.getValueSourceExp())) {
            return false;
        }
        if ("multi-input".equals(tagRuleDef.getValueSourceExp().getValueSourceType())) {
            ValueSourceExp[] exp = ((MultiInputValueSourceExp) tagRuleDef.getValueSourceExp()).getExp();
            for (ValueSourceExp valueSourceExp : exp) {
                if ("auto-js-dict".equals(valueSourceExp.getValueSourceType())
                        || "auto-js".equals(valueSourceExp.getValueSourceType())) {
                    return true;
                }
            }
        }
        return ("auto-js-dict".equals(tagRuleDef.getValueSourceExp().getValueSourceType())
                || "auto-js".equals(tagRuleDef.getValueSourceExp().getValueSourceType()));
    }

    @Override
    public boolean needAutoScore(TagRuleDef tagRuleDef) {
        return true;
    }

    @Override
    public boolean isSumByWeight(TagRuleDef tagRuleDef) {
        return !needValue(tagRuleDef);
    }

    @Override
    public boolean canAdjustScore(TagRuleDef tagRuleDef) {
        return tagRuleDef.level() == 3;
    }

    @Override
    public boolean fe_scoreVisible(TagRuleDef tagRuleDef) {
        return tagRuleDef.level() != TERTIARY_TAG;
    }
}
