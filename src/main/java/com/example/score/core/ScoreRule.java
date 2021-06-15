package com.example.score.core;

import org.springframework.util.StringUtils;

/**
 * @Description TODO
 * @Date 2021/4/3 11:23
 * @Created by hdw
 */
public interface ScoreRule extends ScoreCalculator {

    String[] EMPTY_ARRAY = new String[0];
    String ALL_REMAINS = "!*";
    int NON_EXCLUSIVE_MATCH_SCORE = -1;

    ScoreRuleType scoreRuleType();

    double exclusiveMatchScore();

    default boolean hasExclusiveMatch() {
        return exclusiveMatchScore() != NON_EXCLUSIVE_MATCH_SCORE;
    }

    default boolean canHandle(ScoreRuleType scoreRuleType) {
        return scoreRuleType() == scoreRuleType;
    }

    default String[] splitValueAndScore(String valueScoreMapping) {
        if (org.apache.commons.lang.StringUtils.isEmpty(valueScoreMapping)) {
            throw new IllegalArgumentException("valueScoreMapping cannot be null or empty");
        }
        valueScoreMapping = valueScoreMapping.trim();
        int index = valueScoreMapping.indexOf(ScoreRuleConfig.SCORE_MAPPING_SPLIT);
        if (index == -1) {
            return new String[]{valueScoreMapping};
        }
        int occ = StringUtils.countOccurrencesOf(valueScoreMapping, ScoreRuleConfig.SCORE_MAPPING_SPLIT);
        if (occ > 1) {
            throw new RuleExprInvalidException("valueScoreMapping cannot contain multiple value-score split string[" +
                    valueScoreMapping + "]");
        }
        return new String[]{
                valueScoreMapping.substring(0, index).trim(),
                valueScoreMapping.substring(index + ScoreRuleConfig.SCORE_MAPPING_SPLIT.length()).trim()
        };
    }

    default void trimItem(String[] arr) {
        if (null == arr || arr.length == 0) {
            return;
        }
        for (int i = 0; i < arr.length; ++i) {
            arr[i] = null == arr[i] ? null : arr[i].trim();
        }
    }

    default String[] splitSet(String val) {
        if (org.apache.commons.lang.StringUtils.isEmpty(val)) {
            return EMPTY_ARRAY;
        }
        String[] set = val.split(ScoreRuleConfig.VALUE_SPLIT);
        trimItem(set);
        return set;
    }

    default boolean isAllRemains(String val) {
        return ALL_REMAINS.equals(val);
    }
}
