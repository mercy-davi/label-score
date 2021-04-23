package com.example.labelscore.core;

import com.example.labelscore.bean.TagRuleDef;
import com.example.labelscore.bean.TagRuleInstance;

import java.util.Objects;

/**
 * @Description TODO
 * @Date 2021/4/3 11:20
 * @Created by hdw
 */
public abstract class AbstractScoreRule implements ScoreRule {
    protected final TagRuleInstance ruleInstance;
    protected final ScoreRuleType handleScoreRuleType;

    protected AbstractScoreRule(TagRuleInstance ruleInstance, ScoreRuleType scoreRuleType) {
        this.ruleInstance = Objects.requireNonNull(ruleInstance);
        this.handleScoreRuleType = scoreRuleType;
    }

    @Override
    public ScoreRuleType scoreRuleType() {
        return handleScoreRuleType;
    }

    @Override
    public double exclusiveMatchScore() {
        return NON_EXCLUSIVE_MATCH_SCORE;
    }

    String contextInfo() {
        return contextInfo(ruleInstance);
    }

    static String contextInfo(TagRuleInstance ruleInstance) {
        return String.format("{defId:%s, instanceId:%s}", ruleInstance.getScoreRuleDefId(), ruleInstance.getId());
    }

    String contextInfo(String param) {
        return String.format("{defId:%s, instanceId:%s}",
                ruleInstance.getScoreRuleDefId(), ruleInstance.getId(), param);
    }

    Object evalForValue(RuleContext ruleContext) {
        TagRuleDef tagRuleDef = ruleInstance.getDefinition();
        Objects.requireNonNull(tagRuleDef);
        Objects.requireNonNull(tagRuleDef.getScoreExp(), scoreRuleType() + "must have js score exp");
        ValueSourceExp valueSourceExp = tagRuleDef.getValueSourceExp();
        Objects.requireNonNull(valueSourceExp);
        return valueSourceExp.value(ruleContext, ruleInstance, ruleInstance.getValueSource());
    }
}
