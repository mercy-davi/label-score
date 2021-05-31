package com.example.labelscore.service;

import java.util.Map;

/**
 * @Description TODO
 * @Date 2021/4/5 9:56
 * @Created by hdw
 */
public interface ScoreTagConfigService {
    /**----------------------- radar map --------------------------*/
    String CONFIG_KEY_SCORE_RULE_DESC = "score-rule-desc";
    String CONFIG_KEY_DEFAULT_SCORE_VALUE = "default-score-value";
    String CONFIG_KEY_MAX_SCORE_VALUE = "max-score-value";

    /**----------------------- sql --------------------------*/
    String CONFIG_KEY_SQL_TABLE_OF_XXX = "sql-xxx";

    /**----------------------- conditional exist tag --------------------------*/
    String CONFIG_KEY_CONDITIONAL_EXIST_TAG = "conditional-exist-tag";

    /**----------------------- conditional exist tag weight --------------------------*/
    String CONFIG_KEY_CONDITIONAL_EXIST_TAG_WEIGHT = "conditional-exist-tag-weight";

    /**----------------------- conditional exist tag isSuit --------------------------*/
    String CONFIG_KEY_CONDITIONAL_IS_SUIT = "conditional-exist-tag-is-suit";

    /**----------------------- score phase no --------------------------*/
    String CONFIG_KEY_SCORE_PHASE_NO = "score-phase-no";

    /**----------------------- possible-default-value-sentiment --------------------------*/
    String DEFAULT_VALUE_SENTIMENT = "possible-default-value-sentiment";

    /**----------------------- possible-default-value-credit-record --------------------------*/
    String DEFAULT_VALUE_CREDIT_RECORD = "possible-default-value-credit-record";

    /**----------------------- after-need-update-rule-code --------------------------*/
    String AFTER_NEED_UPDATE_RULE_CODE = "after-need-update-rule-code";


    /**
     *
     * @param scoreCardCode 评分卡code
     * @return array means multiple independent conditions, key is condition, value is Tag-Def-Code included collection.
     */
    Map<String, String[]>[] findConditionalTagConfig(String scoreCardCode);

    /**
     *
     * @param scoreCardCode 评分卡code
     * @return array means multiple independent conditions, key is condition, value is (code -> weight) map.
     */
    Map<String, Map<String, Double>>[] findConditionalTagWeightConfig(String scoreCardCode);

    /**
     *
     * @param scoreCardCode 评分卡code
     * @return array means multiple independent conditions, key is condition, value is Tag-Instance-Code included collection.
     */
    Map<String, String[]>[] findConditionalTagIsSuitConfig(String scoreCardCode);

    /**
     *
     * @param keys 关键字
     * @param scoreCardCode 评分卡code
     * @return ScoreRadarMap Config
     */
    Map<String, String>[] findScoreRadarMapConfig(String[] keys, String scoreCardCode);

    /**
     *
     * @param keys 关键字
     * @param scoreCardCode 评分卡code
     * @return AfterNeedUpdateRuleCode Config
     */
    Map<String, String[]>[] findAfterNeedUpdateRuleCodeConfig(String[] keys, String scoreCardCode);

    /**
     *
     * @param keys 关键字
     * @param scoreCardCode 评分卡code
     * @return Default Value Config
     */
    Map<String, String[]>[] findDefaultValueConfig(String[] keys, String scoreCardCode);

    /**
     *
     * @param keys 关键字
     * @param scoreCardCode 评分卡code
     * @return score phase no
     */
    Map<String, String[]>[] findScorePhaseNoConfig(String[] keys, String scoreCardCode);

    Map<String, ?>[] findConfig(Class<?> valueClass, String[] keys, String scoreCardCode);


}
