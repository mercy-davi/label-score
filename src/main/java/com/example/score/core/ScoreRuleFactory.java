package com.example.score.core;

import com.example.score.bean.TagRuleInstance;

/**
 * @Description TODO
 * @Date 2021/4/4 16:03
 * @Created by hdw
 */
public class ScoreRuleFactory {

    public ScoreRule rule(TagRuleInstance ruleInstance) {
        ScoreRuleType ruleType = ruleInstance.getDefinition().getScoreType();
        if (null == ruleType) {
            return null;
        }
        switch (ruleType) {
            case Dict:
            case DictH:
                return new DictScoreRule(ruleInstance, ruleInstance.getDefinition().getScoreExp(), ruleType);
            case Interval:
            case IntervalH:
                return new IntervalScoreRule(ruleInstance, ruleInstance.getDefinition().getScoreExp(), ruleType);
            case Js:
                return new JsScoreRule(ruleInstance);
            case BooleanExpsMultiVal:
            case BooleanExpsMultiValH:
                return new BooleanExpsMultiValScoreRule(ruleInstance, ruleInstance.getDefinition().getScoreExp(), ruleType);
            case TwoDimensionMatrix:
            case TwoDimensionMatrixH:
                return new TwoDimensionMatrixScoreRule(ruleInstance, ruleInstance.getDefinition().getScoreExp(), ruleType);
            case SumByWeight:
                return new SumByWeightScoreRule(ruleInstance, this);
            case ManualInput:
            default:
                return EmptyScoreRule.INSTANCE;
        }
    }

    public static ExclusiveMatchScoreRule rule(TagRuleInstance ruleInstance, ScoreRuleType ruleType, String scoreExp, int levels) {
        if (null == ruleType) {
            return null;
        }
        switch (ruleType) {
            case Dict:
            case DictH:
                return new DictScoreRule(ruleInstance, scoreExp, ruleType, levels);
            case Interval:
            case IntervalH:
                return new IntervalScoreRule(ruleInstance, scoreExp, ruleType, levels);
            case BooleanExpsMultiVal:
            case BooleanExpsMultiValH:
//                return new BooleanExpsMultiValScoreRule(null, scoreExp, ruleType, levels);
            default:
                throw new IllegalStateException("should not be here");
        }
    }
}
