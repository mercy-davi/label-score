package com.example.labelscore.core;

import com.example.labelscore.bean.TagRuleInstance;

import javax.script.ScriptException;

import static com.example.labelscore.core.RuleContext.TAG_VALUE;
import static com.example.labelscore.core.ValueSourceExp.EXCEPTION_FALLBACK_VALUE;

/**
 * @Description TODO
 * @Date 2021/4/3 23:12
 * @Created by hdw
 */
public class JsScoreRule extends AbstractScoreRule {

    public JsScoreRule(TagRuleInstance ruleInstance) {
        super(ruleInstance, ScoreRuleType.Js);
    }

    @Override
    public Double calc(RuleContext ruleContext) {
        Object value = evalForValue(ruleContext);
        if (value == EXCEPTION_FALLBACK_VALUE) {
            return null;
        }

        ruleContext.getBindings().put(TAG_VALUE, value);
        try {
            Object result = JSEngine.get().eval(this.ruleInstance.getDefinition().getScoreExp(), ruleContext.getBindings());
            if (result instanceof Number) {
                double score = ((Number) result).doubleValue();
                if (ScoreRuleConfig.SCORE_RANGE.contains(score)) {
                    return score;
                }
                throw new RuleEvalException("eval js score rule result is " + score + ", which is not in the range of ScoreRuleConfig");
            } else {
                if (null == result) {
                    return null;
                }
                throw new RuleEvalException("eval js score rule result is not a Number, " + contextInfo());
            }
        } catch (ScriptException ex) {
            if (null == value) {
                return null;
            }
            throw new RuleEvalException("eval js score rule error, " + contextInfo(), ex);
        } finally {
            ruleContext.getBindings().remove(TAG_VALUE);
        }
    }
}
