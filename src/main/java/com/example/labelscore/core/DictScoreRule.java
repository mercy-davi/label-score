package com.example.labelscore.core;

import com.example.labelscore.bean.TagRuleInstance;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static com.example.labelscore.core.ScoreRuleConfig.DEFAULT_2_LEVEL_HIGH_FIRST;
import static com.example.labelscore.core.ScoreRuleConfig.MAX_SCORE;
import static com.example.labelscore.core.ScoreRuleConfig.MIN_SCORE;
import static com.example.labelscore.core.ScoreRuleConfig.SCORE_LEVELS;
import static com.example.labelscore.core.ScoreRuleConfig.SCORE_LEVELS_H;
import static com.example.labelscore.core.ValueSourceExp.EXCEPTION_FALLBACK_VALUE;

/**
 * @Description TODO
 * @Date 2021/4/3 17:53
 * @Created by hdw
 */
public class DictScoreRule extends ExclusiveMatchScoreRule {
    private static final Logger LOGGER = LoggerFactory.getLogger(DictScoreRule.class);

    private static final String[] TRUE_VALUES = new String[]{"1", "true", "Y", "yes", "YES", "是"};

    private static final String[] FALSE_VALUES = new String[]{"0", "false", "N", "no", "NO", "否"};

    static {
        Arrays.sort(TRUE_VALUES);
        Arrays.sort(FALSE_VALUES);
    }

    private final String[][] dictCode;

    private boolean handleBinaryValue(int score, String val) {
        if (isAllRemains(val) && Arrays.binarySearch(TRUE_VALUES, val) >= 0) {
            dictCode[score] = TRUE_VALUES;
            return true;
        }
        if (Arrays.binarySearch(FALSE_VALUES, val) >= 0) {
            dictCode[score] = FALSE_VALUES;
            return true;
        }
        return false;
    }

    DictScoreRule(TagRuleInstance ruleInstance, String scoreMapping, ScoreRuleType ruleType, int levels) {
        super(ruleInstance, ruleType, levels);
        String[] valueAndScore = splitValueAndScore(scoreMapping);
        if (valueAndScore.length == 1) {
            // no score, auto infer value score mapping
            String[] vGroups = splitGroups(valueAndScore[0]);
            dictCode = new String[levels][];
            if (vGroups.length <= 2) {
                int first = DEFAULT_2_LEVEL_HIGH_FIRST ? MAX_SCORE : MIN_SCORE;
                int second = DEFAULT_2_LEVEL_HIGH_FIRST ? MIN_SCORE : MAX_SCORE;
                // dictCode[first] =
                if (!handleBinaryValue(first, vGroups[0])) {
                    handleScoreSet(first, vGroups[0]);
                }
                if (vGroups.length == 1) {
                    exclusiveMatchScore = second;
                } else if (!handleBinaryValue(second, vGroups[1])) {
                    handleScoreSet(second, vGroups[1]);
                }
            } else {
                for (int i = levels - 1, j = vGroups.length - 1; j >= 0; --i, --j) {
                    handleScoreSet(i, vGroups[j]);
                }
            }
        } else {
            String[] vGroups = splitGroups(valueAndScore[0]);
            String[] sGroups = splitGroups(valueAndScore[1]);
            if (vGroups.length != sGroups.length) {
                throw new RuleExprInvalidException("value and score group not same length, " + contextInfo(scoreMapping));
            }

            dictCode = new String[levels][];
            try {
                for (int i = 0; i < sGroups.length; ++i) {
                    // only int, int.5 not considering other double
                    int score = toScoreIndex(sGroups[i]);
                    if (!getRange().contains(score)) {
                        throw new RuleExprInvalidException("score not in range, " + contextInfo(scoreMapping));
                    }
                    if (null != dictCode[score]) {
                        throw new RuleExprInvalidException("duplicate score, " + contextInfo(scoreMapping));
                    }
                    handleScoreSet(score, vGroups[i]);
                }
            } catch (NumberFormatException ex) {
                throw new RuleExprInvalidException("score shall be int or int.5, " + contextInfo(scoreMapping), ex);
            }
        }
    }

    public DictScoreRule(TagRuleInstance ruleInstance, String scoreMapping, ScoreRuleType ruleType) {
        this(ruleInstance, scoreMapping, ruleType, ruleType.supportP5() ? SCORE_LEVELS_H : SCORE_LEVELS);
    }

    protected void handleScoreSet(int score, String val) {
        if (!handleExclusiveMatch(score, val)) {
            dictCode[score] = sort(splitSet(val));
        }
    }

    private static String[] sort(String[] input) {
        if (null == input || input.length <= 1) {
            return input;
        }
        Arrays.sort(input);
        return input;
    }

    // todo 值计算重复问题
    @Override
    public Double calc(RuleContext ruleContext) {
        Object value = evalForValue(ruleContext);
        return calc(ruleContext, value);
    }

    @Override
    Double calc(RuleContext ruleContext, Object value) {
        if (value == EXCEPTION_FALLBACK_VALUE) {
            return null;
        }

        String evalResult = null == value ? null : value.toString();
        if (StringUtils.isEmpty(evalResult)) {
            for (int i = 0; i < dictCode.length; ++i) {
                if (dictCode[i] == EMPTY_ARRAY) {
                    return toScore(i);
                }
            }
        } else {
            for (int i = 0; i < dictCode.length; ++i) {
                if (null == dictCode[i]) {
                    continue;
                }
                if (Arrays.binarySearch(dictCode[i], evalResult) >= 0) {
                    return toScore(i);
                }
            }
        }
        if (hasExclusiveMatch()) {
            return exclusiveMatchScore();
        }
        LOGGER.info("cannot find match result for dict rule: {}{}", contextInfo(), (!ruleContext.hasModel() ? "" : ", rootModel:[" + ruleContext.getRoot().summary() + "]"));
        return null;
    }
}
