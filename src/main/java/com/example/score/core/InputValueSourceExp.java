package com.example.score.core;

import com.example.score.bean.TagRuleInstance;

/**
 * @Classname InputValueSourceExp
 * @Description TODO
 * @Date 2021/3/31
 * @Author hdw
 */
public class InputValueSourceExp extends ValueSourceExp {

    public InputValueSourceExp() {
        this.valueSourceType = VALUE_SOURCE_TYPE_INPUT;
    }

    @Override
    public boolean isAuto() {
        return false;
    }

    @Override
    public Object value(RuleContext context, TagRuleInstance tagRuleInstance, String valueSourceStr) {
        return this.dateType.converter().convert(valueSourceStr);
    }
}
