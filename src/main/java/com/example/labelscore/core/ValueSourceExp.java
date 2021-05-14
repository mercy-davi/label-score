package com.example.labelscore.core;

import com.example.labelscore.bean.TagRuleInstance;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import static com.example.labelscore.core.ScoreRuleConfig.VALUE_SPLIT;

/**
 * @Classname ValueSourceExp
 * @Description TODO
 * @Date 2021/3/31
 * @Author hdw
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "valueSourceType",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AutoJsValueSourceExp.class, name = ValueSourceExp.VALUE_SOURCE_TYPE_AUTO_JS),
        @JsonSubTypes.Type(value = AutoJsDictValueSourceExp.class, name = ValueSourceExp.VALUE_SOURCE_TYPE_AUTO_JS_DICT),
        @JsonSubTypes.Type(value = InputValueSourceExp.class, name = ValueSourceExp.VALUE_SOURCE_TYPE_INPUT),
        @JsonSubTypes.Type(value = MultiInputValueSourceExp.class, name = ValueSourceExp.MULTI_VALUE_SOURCE_TYPE_INPUT)
})
public abstract class ValueSourceExp {
    public static final Object EXCEPTION_FALLBACK_VALUE = new Object();
    public static final String MULTI_VALUE_SPLIT = ",";

    protected String valueSourceType;

    protected Object exp;

    protected DataType dateType = DataType.STRING;

    protected String unit;

    protected Object select;

    public String getValueSourceType() {
        return valueSourceType;
    }

    public void setValueSourceType(String valueSourceType) {
        this.valueSourceType = valueSourceType;
    }

    public abstract boolean isAuto();

    // @JsonRawValue
    public Object getExp() {
        return exp;
    }

    public void setExp(Object exp) {
        this.exp = exp;
    }

    public abstract Object value(RuleContext context, TagRuleInstance tagRuleInstance, String valueSourceStr);

    static final String VALUE_SOURCE_TYPE_AUTO_JS = "auto-js";
    static final String VALUE_SOURCE_TYPE_AUTO_JS_DICT = "auto-js-dict";
    static final String VALUE_SOURCE_TYPE_INPUT = "input";
    static final String MULTI_VALUE_SOURCE_TYPE_INPUT = "multi-input";
    static final String VALUE_SOURCE_TYPE_AUTO_SQL = "auto-sql";

    public static boolean isExceptionFallbackValue(Object v) {
        return v == EXCEPTION_FALLBACK_VALUE;
    }

    public static String toStr(Object val) {
        return null == val ? null : val.getClass().isArray()
                ? valueArrayToStr((Object[]) val) : val.toString();
    }

    public static String valueArrayToStr(Object[] values) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; ++i) {
            if (null != values[i]) {
                if (values[i] instanceof String) {
                    sb.append(((String) values[i]).trim());
                } else {
                    sb.append(values[i].toString());
                }
            }
            if (i != values.length - 1) {
                sb.append(VALUE_SPLIT);
            }
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object valueSourceExp) {
        if (this == valueSourceExp) {
            return true;
        }
        if (!(valueSourceExp instanceof ValueSourceExp)) {
            return false;
        }
        ValueSourceExp that = (ValueSourceExp) valueSourceExp;
        return this.valueSourceType.equals(that.valueSourceType)
                && this.exp.equals(that.exp);
    }

    public DataType getDateType() {
        return dateType;
    }

    public void setDateType(DataType dateType) {
        this.dateType = dateType;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Object getSelect() {
        return select;
    }

    public void setSelect(Object select) {
        this.select = select;
    }
}
