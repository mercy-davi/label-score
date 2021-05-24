package com.example.labelscore.service;

import java.util.Map;

/**
 * @Description TODO
 * @Date 2021/4/4 20:36
 * @Created by hdw
 */
public interface ContextStrMaker {
    String DEFAULT_KEY_PREFIX = "key";
    String make(String key, Object value);
    String make(String key1, Object value1, String key2, Object value2);
    String make(String key1, Object value1, String key2, Object value2, String key3, Object value3);
    String make(String[] keys, Object[] values);
    String make(Map<String, Object> mapContext);
    String make(Object... values);
}
