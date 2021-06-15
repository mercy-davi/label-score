package com.example.score.core;

import com.example.score.bean.TagRuleInstance;
import com.example.score.constants.LabelConstants;
import org.apache.commons.lang.StringUtils;

/**
 * @Classname AutoJsDictValueSourceExp
 * @Description TODO
 * @Date 2021/3/31
 * @Author hdw
 */
public class AutoJsDictValueSourceExp extends AutoJsValueSourceExp {
    private String dictType;

    public AutoJsDictValueSourceExp() {
        this.valueSourceType = VALUE_SOURCE_TYPE_AUTO_JS_DICT;
    }

    @Override
    public Object value(RuleContext context, TagRuleInstance tagRuleInstance, String valueSourceStr) {
        Object value = super.value(context, tagRuleInstance, valueSourceStr);
        if (LabelConstants.AFTER_LABEL_SCORE.equals(context.getObjInContextBinding("after"))
                && !StringUtils.isEmpty(valueSourceStr) && valueSourceStr.equals(value.toString())) {
            return valueSourceStr;
        }
        if (value == EXCEPTION_FALLBACK_VALUE) {
            return value;
        }
        if (null != value) {
            DataType.DictConverter dictConverter = (DataType.DictConverter) DataType.DICT.converter();
            return dictConverter.convert(context, dictType, value.toString());
        }
        return null;
    }

    public String getDictType() {
        return dictType;
    }

    public void setDictType(String dictType) {
        this.dictType = dictType;
    }
}
