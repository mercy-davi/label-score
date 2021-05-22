package com.example.labelscore.constants;

/**
 * @Description 标签评价配置类基类
 * 主要配置接口所需的常量，以及实现PE与FP的差异化
 * @Date 2021/4/3 11:19
 * @Created by hdw
 */
public class LabelConstants {
    /**
     * 投前 score card code
     */
    public static final String FP_THREE_LEVEL_LABEL_EVALUATION = "after_common_label_evaluation";
    public static final String FP_ESTATE_MORTGAGE_DEBT = "fp_estate_mortgage_debt";
    public static final String FP_ESTATE_CREDIT_DEBT = "fp_estate_credit_debt";
    public static final String GOVERNMENT_FINANCING = "fp_government_financing";
    /**
     * 投后 score card code
     */
    public static final String AFTER_COMMON_LABEL_EVALUATION = "after_common_label_evaluation";
    public static final String AFTER_ESTATE_MORTGAGE_DEBT = "after_estate_mortgage_debt";
    public static final String AFTER_ESTATE_CREDIT_DEBT = "after_estate_credit_debt";
    public static final String AFTER_GOVERNMENT_FINANCING = "after_government_financing";

    public static final String AFTER_LABEL_SCORE = "afterLabelScore";

    /**
     * score_type
     */
    public static final String SCORE_TYPE_SETUP = "scoreTypeSetup";
    public static final String SCORE_TYPE_VOTING = "scoreTypeVoting";
    public static final String SCORE_TYPE_DECISION = "scoreTypeDecision";
    public static final String SCORE_TYPE_REEVALUATE = "scoreTypeReevaluate";



    public static final String DEFAULT_VALUE_RULE_CODE = "ruleCode";
    public static final String DEFAULT_VALUE = "defaultValue";

    /**
     * 投后打分需要实时更新的配置从标签 ruleCode
     */
    public static final String RULE_CODE = "ruleCode";

}
