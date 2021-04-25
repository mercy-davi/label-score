package com.example.labelscore.core;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @Description TODO
 * @Date 2021/4/3 16:13
 * @Created by hdw
 */
public enum DataType {
    STRING(new StringConverter()),
    NUMBER(new NumberConverter()),
    PERCENT(new PercentConverter()),
    DICT(new DictConverter());

    private final Converter<?> converter;

    <T> DataType(Converter<T> converter) {
        this.converter = converter;
    }

    public Converter<?> converter() {
        return converter;
    }

    public abstract static class Converter<T> {
        public abstract T convert(String strValue);

        static final String[] VALUE_SOURCE_SPLIT = new String[]{"-", "~"};

        static final String NEGATIVE_FLAG = "-";

        static final String HUNDRED_MILLION = "亿";

        static final String VALUE_SOURCE_PERCENT = "%";

        static final String[] VALUE_SOURCE_LESS = new String[]{"<", "低于", "以下", "前"};

        static final String[] VALUE_SOURCE_MORE = new String[]{">", "超过", "以上"};

        static final Pattern pattern = Pattern.compile("[^\\-?\\d.]");

        Number strToNumber(String string, boolean isPercent) {
            Number number = NumberUtils.createNumber(pattern.matcher(string).replaceAll("").trim());
            if (number.doubleValue() < 1 && isPercent) {
                return number.doubleValue() * 100;
            }
            return number;
        }

        Number strToNumber(String[] strings, boolean isPercent) {
            if (strings.length == 0) {
                return null;
            }
            double result = 0;
            for (String str : strings) {
                Number number = NumberUtils.createNumber(pattern.matcher(str).replaceAll("").trim());
                if (number.doubleValue() < 1 && number.doubleValue() > 0 && isPercent) {
                    result += number.doubleValue() * 100;
                } else {
                    result += number.doubleValue();
                }
            }
            return result / strings.length;
        }

        boolean anyMatch(String value, String[] valueSource) {
            return Arrays.stream(valueSource).anyMatch(value::contains);
        }

        String findSplit(String value, String[] valueSource) {
            for (String str : valueSource) {
                if (value.contains(str)) {
                    return str;
                }
            }
            return null;
        }
    }

    public static class StringConverter extends Converter<String> {
        @Override
        public String convert(String strValue) {
            return StringUtils.isBlank(strValue) ? null : strValue.trim();
        }
    }

    public static class NumberConverter extends Converter<Number> {
        @Override
        public Number convert(String strValue) {
            if (StringUtils.isBlank(strValue)) {
                return null;
            }
            strValue = strValue.trim();
            if (anyMatch(strValue, VALUE_SOURCE_SPLIT) && !strValue.startsWith(NEGATIVE_FLAG)) {
                Number number = strToNumber(strValue.split(findSplit(strValue, VALUE_SOURCE_SPLIT)), false);
                if (strValue.contains(HUNDRED_MILLION)) {
                    return number.doubleValue() * 10000;
                }
                return number;
            }
            if (anyMatch(strValue, VALUE_SOURCE_LESS)) {
                return strToNumber(strValue, false).doubleValue() - 1;
            }
            if (anyMatch(strValue, VALUE_SOURCE_MORE)) {
                return strToNumber(strValue, false).doubleValue() + 1;
            }
            try {
                String trim = pattern.matcher(strValue).replaceAll("").trim();
                return NumberUtils.createNumber(trim);
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }

    public static class PercentConverter extends Converter<Number> {
        // return 0-100
        @Override
        public Number convert(String strValue) {
            if (StringUtils.isBlank(strValue)) {
                return null;
            }
            strValue = strValue.trim();
            if (anyMatch(strValue, VALUE_SOURCE_SPLIT) && !strValue.startsWith(NEGATIVE_FLAG)) {
                return strToNumber(strValue.split(findSplit(strValue, VALUE_SOURCE_SPLIT)), true);
            }
            if (anyMatch(strValue, VALUE_SOURCE_LESS)) {
                return strToNumber(strValue, true).doubleValue() - 1;
            }
            if (anyMatch(strValue, VALUE_SOURCE_MORE)) {
                return strToNumber(strValue, true).doubleValue() + 1;
            }
            if (strValue.endsWith(VALUE_SOURCE_PERCENT)) {
                return NumberUtils.createNumber(strValue.substring(0, strValue.length() - 1));
            }
            try {
                String trim = pattern.matcher(strValue).replaceAll("").trim();
                return NumberUtils.createNumber(trim);
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }

    public static class ArrayConverter extends Converter<Object[]> {
        @Override
        public Object[] convert(String strValue) {
            return new Object[0];
        }
    }

    public static class DictConverter extends Converter<String> {
        @Override
        public String convert(String strValue) {
            throw new IllegalStateException("should not be called");
        }

        public String convert(RuleContext ruleContext, String dictType, String strValue) {
            RootModel rootModel = ruleContext.getRoot();
            Map<String, DictInfo> map;
            if (null == rootModel.getDictInfoMap() || null == (map = rootModel.getDictInfoMap().get(dictType))) {
                return strValue;
            }
            DictInfo dictInfo = map.get(strValue);
            return null == dictInfo ? strValue : dictInfo.getDictName().trim();
        }
    }
}
