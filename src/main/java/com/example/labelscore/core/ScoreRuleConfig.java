package com.example.labelscore.core;

import com.example.labelscore.util.IntRange;
import com.example.labelscore.util.Range;

/**
 * @Classname ScoreRuleConfig
 * @Description TODO
 * @Date 2021/3/31
 * @Author hdw
 */
public class ScoreRuleConfig {

    private ScoreRuleConfig() {
        throw new IllegalStateException("Utility class");
    }

    public static final int MAX_SCORE = 5;
    public static final int MIN_SCORE = 0;
    public static final int SCORE_LEVELS = MAX_SCORE - MIN_SCORE + 1;
    public static final int SCORE_LEVELS_H = 2 * (MAX_SCORE - MIN_SCORE) + 1;
    public static final String SCORE_MAPPING_GROUP_SPLIT = ";";
    public static final String SCORE_MAPPING_SPLIT = "->";
    public static final String VALUE_SPLIT = ",";
    public static final Range SCORE_RANGE = new IntRange(MIN_SCORE, MAX_SCORE, Range.CLOSED_CLOSED);
    public static final Range SCORE_RANGE_H = new IntRange(MIN_SCORE, MAX_SCORE * 2, Range.CLOSED_CLOSED);
    public static final boolean DEFAULT_2_LEVEL_HIGH_FIRST = true;
    public static final String MULTI_DIMENSION_DEF_SPLIT = "\\n";
    public static final String MULTI_DIMENSION_SPLIT = "\\\\";
    public static final String MULTI_DIMENSION_MAPPING_SPLIT = "/";
    public static final String P5 = ".5";
    public static final String MULTI_SAME_SCORE_SPLIT = "||";
    public static final String MULTI_ESCAPE_SPLIT = "\\|\\|";
    public static final String ALL_REMAINS = "!*";
}
