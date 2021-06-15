package com.example.score.core;

import com.example.score.bean.TagRuleInstance;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptException;

import static com.example.score.core.RuleContext.TAG_VALUE;
import static com.example.score.core.ScoreRuleConfig.DEFAULT_2_LEVEL_HIGH_FIRST;
import static com.example.score.core.ScoreRuleConfig.MAX_SCORE;
import static com.example.score.core.ScoreRuleConfig.MIN_SCORE;
import static com.example.score.core.ScoreRuleConfig.SCORE_LEVELS;
import static com.example.score.core.ScoreRuleConfig.SCORE_LEVELS_H;

/**
 * @Description TODO
 * @Date 2021/4/3 14:59
 * @Created by hdw
 */
public class BooleanExpsMultiValScoreRule extends ExclusiveMatchScoreRule {
    private static final Logger LOGGER = LoggerFactory.getLogger(BooleanExpsMultiValScoreRule.class);

    private final String[] expressions;

    BooleanExpsMultiValScoreRule(TagRuleInstance ruleInstance, String scoreMapping, ScoreRuleType ruleType, int levels) {
        super(ruleInstance, ruleType, levels);
        String[] valueAndScore = splitValueAndScore(scoreMapping);
        if (valueAndScore.length == 1) {
            // no score, auto infer value score mapping
            String[] vGroups = splitGroups(valueAndScore[0]);
            expressions = new String[levels];
            if (vGroups.length <= 2) {
                int first = DEFAULT_2_LEVEL_HIGH_FIRST ? MAX_SCORE : MIN_SCORE;
                int second = DEFAULT_2_LEVEL_HIGH_FIRST ? MIN_SCORE : MAX_SCORE;
                handleExp(first, vGroups[0]);
                if (vGroups.length == 1) {
                    exclusiveMatchScore = second;
                } else {
                    handleExp(second, vGroups[1]);
                }
            } else {
                for (int i = levels - 1, j = vGroups.length - 1; j >= 0; --i, --j) {
                    handleExp(i, vGroups[j]);
                }
            }
        } else {
            String[] vGroups = splitGroups(valueAndScore[0]);
            String[] sGroups = splitGroups(valueAndScore[1]);
            if (vGroups.length != sGroups.length) {
                throw new RuleExprInvalidException("value and score group not same length, " + contextInfo(scoreMapping));
            }

            expressions = new String[levels];
            try {
                for (int i = 0; i < sGroups.length; ++i) {
                    // only int, int.5 not considering other double
                    int score = toScoreIndex(sGroups[i]);
                    if (!getRange().contains(score)) {
                        throw new RuleExprInvalidException("score not in range, " + contextInfo(scoreMapping));
                    }
                    if (null != expressions[score]) {
                        throw new RuleExprInvalidException("duplicate score, " + contextInfo(scoreMapping));
                    }
                    handleExp(score, vGroups[i]);
                }
            } catch (NumberFormatException ex) {
                throw new RuleExprInvalidException("score shall be int or int.5, " + contextInfo(scoreMapping), ex);
            }
        }
    }

    public BooleanExpsMultiValScoreRule(TagRuleInstance ruleInstance, String scoreMapping, ScoreRuleType ruleType) {
        this(ruleInstance, scoreMapping, ruleType, ruleType.supportP5() ? SCORE_LEVELS_H : SCORE_LEVELS);
    }

    private void handleExp(int score, String exp) {
        if (StringUtils.isEmpty(exp)) {
            throw new RuleExprInvalidException("BooleanExpsMultiVal score expressions cannot be blank");
        }
        if (!handleExclusiveMatch(score, exp)) {
            expressions[score] = exp;
        }
    }

    @Override
    public Double calc(RuleContext ruleContext) {
        Object value  = evalForValue(ruleContext);
        return calc(ruleContext, value);
    }

    @Override
    Double calc(RuleContext ruleContext, Object value) {
        if (!(value instanceof Object[])) {
            return null;
        }
        try {
            ruleContext.getBindings().put(TAG_VALUE, value);

            for (int i = 0; i < expressions.length; ++i) {
                if (null == expressions[i]) {
                    continue;
                }
                Object evalResult = JSEngine.get().eval(expressions[i], ruleContext.getBindings());
                if (evalResult instanceof Boolean && Boolean.TRUE.equals(evalResult)) {
                    return toScore(i);
                }
            }
            if (hasExclusiveMatch()) {
                return exclusiveMatchScore();
            }
            LOGGER.info("cannot find match result for dict rule: {}{}", contextInfo(), (!ruleContext.hasModel() ? "" : ", rootModel:[" + ruleContext.getRoot().summary() + "]"));
            return null;
        } catch (ScriptException ex) {
            throw new IllegalStateException("eval expression error", ex);
        } finally {
            ruleContext.getBindings().remove(TAG_VALUE);
        }
    }
}
