package com.example.labelscore.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Classname JsonUtil
 * @Description TODO
 * @Date 2021/4/9
 * @Author hdw
 */
public class JsonUtil {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private JsonUtil() {
        throw new IllegalStateException("Utility class");
    }

    static {
        OBJECT_MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public static String toJsonString(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("convert object to json string error", ex);
        }
    }

    public static <T> T jsonToBean(String jsonStr, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(jsonStr, clazz);
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static <T> T jsonToBeanList(String jsonStr, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(jsonStr,
                    OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static <K, V> Map<K, V> jsonToMap(String jsonStr, Class<K> keyType, Class<V> valueType) {
        try {
            return OBJECT_MAPPER.readValue(jsonStr, OBJECT_MAPPER.getTypeFactory().constructMapType(HashMap.class, keyType, valueType));
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static <K, V> Map<K, V> jsonToMap(String jsonStr, Class<K> keyType, Class<V> valueRawType, Class<?>... valueParamType) {
        try {
            return OBJECT_MAPPER.readValue(jsonStr, OBJECT_MAPPER.getTypeFactory().constructMapType(HashMap.class,
                    OBJECT_MAPPER.getTypeFactory().constructType(keyType),
                    OBJECT_MAPPER.getTypeFactory().constructParametricType(valueRawType, valueParamType))
            );
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
