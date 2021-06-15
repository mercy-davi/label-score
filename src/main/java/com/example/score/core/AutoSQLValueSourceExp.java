package com.example.score.core;

import com.example.score.bean.TagRuleInstance;

/**
 * @Description TODO
 * @Date 2021/4/3 14:58
 * @Created by hdw
 */
public class AutoSQLValueSourceExp extends ValueSourceExp {

    @Override
    public boolean isAuto() {
        return true;
    }

    @Override
    public Object value(RuleContext context, TagRuleInstance tagRuleInstance, String valueSourceStr) {
        return null;
    }
}
