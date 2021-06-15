package com.example.score.core;

import com.example.score.bean.TagRuleInstance;

import java.util.Arrays;

import static com.example.score.core.ScoreRuleConfig.VALUE_SPLIT;

/**
 * @Classname MultiInputValueSourceExp
 * @Description TODO
 * @Date 2021/3/31
 * @Author hdw
 */
public class MultiInputValueSourceExp extends ValueSourceExp {
    @Override
    public boolean isAuto() {
        return false;
    }

    @Override
    public ValueSourceExp[] getExp() {
        return (ValueSourceExp[]) exp;
    }

    public void setExp(ValueSourceExp[] exp) {
        this.exp = exp;
    }

    @Override
    public Object value(RuleContext context, TagRuleInstance tagRuleInstance, String valueSourceStr) {
        ValueSourceExp[] array = getExp();
        if (null == array || array.length == 0) {
            return null;
        }
        Object[] values = new Object[array.length];
        for (int i = 0; i < array.length; ++i) {
            if (null == valueSourceStr) {
                if (ValueSourceExp.VALUE_SOURCE_TYPE_AUTO_JS.equals(array[i].getValueSourceType())) {
                    values[i] = array[i].value(context, tagRuleInstance, null);
                } else {
                    values[i] = "";
                }
            } else {
                String[] valueStrArr = valueSourceStr.split(VALUE_SPLIT, -1);
                values[i] = array[i].value(context, tagRuleInstance, valueStrArr[i]);
            }
        }
        return values;
    }

    @Override
    public boolean equals(Object valueSourceExp) {
        if (this == valueSourceExp) {
            return true;
        }
        if (!(valueSourceExp instanceof MultiInputValueSourceExp)) {
            return false;
        }
        MultiInputValueSourceExp that = (MultiInputValueSourceExp) valueSourceExp;
        return Arrays.equals(getExp(), that.getExp());
    }
}
