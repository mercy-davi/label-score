package com.example.labelscore.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrBuilder;

import java.util.Map;

/**
 * @Description TODO
 * @Date 2021/4/4 20:41
 * @Created by hdw
 */
public class DefaultContextStrMaker implements ContextStrMaker {
    private static final String CONTEXT_KEY_BLANK_MESSAGE = "contextKey cannot be blank";
    private static final String CONTEXT_VALUE_NULL_MESSAGE = "contextValue cannot be null";

    static final String PAIR_SPLIT = ",";
    static final String KEY_VALUE_SPLIT = ":";

    @Override
    public String make(String key, Object value) {
        return String.format("%s:%s",
                requireNotBlank(key),
                requireNotEmpty(value)
        );
    }

    @Override
    public String make(String key1, Object value1, String key2, Object value2) {
        return String.format("%s:%s,%s:%s",
                requireNotBlank(key1),
                requireNotEmpty(value1),
                requireNotBlank(key2),
                requireNotEmpty(value2)
        );
    }

    @Override
    public String make(String key1, Object value1, String key2, Object value2, String key3, Object value3) {
        return String.format("%s:%s,%s:%s,%s:%s",
                requireNotBlank(key1),
                requireNotEmpty(value1),
                requireNotBlank(key2),
                requireNotEmpty(value2),
                requireNotBlank(key3),
                requireNotEmpty(value3)
        );
    }

    @Override
    public String make(String[] keys, Object[] values) {
        if (null == keys || keys.length == 0 || null == values || values.length == 0) {
            throw new IllegalArgumentException("parameter array should not be empty");
        }
        if (keys.length != values.length) {
            throw new IllegalArgumentException("parameter array length should be same");
        }
        StrBuilder sb = new StrBuilder();
        for (int i = 0; i < keys.length; ++i) {
            sb.append(requireNotBlank(keys[i]))
                    .append(KEY_VALUE_SPLIT)
                    .append(requireNotEmpty(values[i]));
            if (i != keys.length - 1) {
                sb.append(PAIR_SPLIT);
            }
        }
        return sb.toString();
    }

    @Override
    public String make(Map<String, Object> mapContext) {
        if (null == mapContext || mapContext.isEmpty()) {
            throw new IllegalArgumentException("mapContext should not be empty");
        }
        StrBuilder sb = new StrBuilder();
        mapContext.forEach((k, v) -> sb.append(requireNotBlank(k))
                .append(KEY_VALUE_SPLIT)
                .append(requireNotEmpty(v))
                .append(PAIR_SPLIT));
        return sb.deleteCharAt(sb.length() - 1).toString();
    }

    @Override
    public String make(Object... values) {
        if (null == values || values.length == 0) {
            throw new IllegalArgumentException("values cannot be empty");
        }
        String[] keys = new String[values.length];
        StrBuilder sb = new StrBuilder();
        for (int i = 0; i < keys.length; ++i) {
            sb.append(DEFAULT_KEY_PREFIX).append(i + 1).append(KEY_VALUE_SPLIT).append(requireNotEmpty(values[i]));
            if (i != keys.length - 1) {
                sb.append(PAIR_SPLIT);
            }
        }
        return sb.toString();
    }

    private static String requireNotBlank(String key) {
        if (StringUtils.isEmpty(key)) {
            throw new IllegalArgumentException(CONTEXT_KEY_BLANK_MESSAGE);
        }
        key = key.trim();
        if (key.length() == 0) {
            throw new IllegalArgumentException(CONTEXT_KEY_BLANK_MESSAGE);
        }
        return key;
    }

    private static Object requireNotEmpty(Object obj) {
        if (null == obj) {
            throw new IllegalArgumentException(CONTEXT_VALUE_NULL_MESSAGE);
        }
        if (obj instanceof String) {
            return requireNotBlank((String) obj);
        }
        return obj;
    }
}
