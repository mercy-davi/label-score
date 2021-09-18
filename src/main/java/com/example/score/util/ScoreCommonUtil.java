package com.example.score.util;

import org.apache.commons.lang.ArrayUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @Classname ScoreCommonUtil
 * @Description TODO
 * @Date 2021/4/9
 * @Author hdw
 */
public class ScoreCommonUtil {

    private ScoreCommonUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 判断数组或多个数组是否包含指定元素
     * @param arg 指定元素
     * @param arrays 数组
     * @return boolean
     */
    public static boolean arrayCheck(String arg, String[]... arrays) {
        if (null == arg || arrays == null || arrays.length == 0) {
            return false;
        }
        if (arrays.length == 1) {
            return ArrayUtils.contains(arrays, arg);
        }
        for (String[] num : arrays) {
            if (ArrayUtils.contains(num, arg)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 合并数组
     * @param array 数组1
     * @param otherArray 数组2
     * @return String[]
     */
    public static String[] mergeArray(String[] array, String[] otherArray) {
        array = ArrayUtils.nullToEmpty(array);
        otherArray = ArrayUtils.nullToEmpty(otherArray);

        int length1 = array.length;
        int length2 = otherArray.length;

        array = Arrays.copyOf(array, length1 + length2);
        System.arraycopy(otherArray, 0 , array, length1, length2);
        return array;
    }

    public static List<String> tagConfigMaps2List(Map<String, String[]>[] maps) {
        if (ArrayUtils.isEmpty(maps)) {
            return Collections.emptyList();
        }
        String key = null;
        for (Map.Entry<String, ?> entry : maps[0].entrySet()) {
            key = entry.getKey();
        }
        return Arrays.asList(maps[0].get(key));
    }
}
