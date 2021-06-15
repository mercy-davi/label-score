package com.example.score.core;

import com.example.score.bean.TagRuleInstance;
import com.example.score.util.DoubleRange;
import com.example.score.util.IntRange;
import com.example.score.util.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.score.core.ScoreRuleConfig.DEFAULT_2_LEVEL_HIGH_FIRST;
import static com.example.score.core.ScoreRuleConfig.MAX_SCORE;
import static com.example.score.core.ScoreRuleConfig.MIN_SCORE;
import static com.example.score.core.ScoreRuleConfig.SCORE_LEVELS;
import static com.example.score.core.ScoreRuleConfig.SCORE_LEVELS_H;
import static com.example.score.core.ValueSourceExp.EXCEPTION_FALLBACK_VALUE;

/**
 * @Description TODO
 * @Date 2021/4/3 21:44
 * @Created by hdw
 */
public class IntervalScoreRule extends ExclusiveMatchScoreRule {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntervalScoreRule.class);

    private final Range[] intervals;

    IntervalScoreRule(TagRuleInstance ruleInstance, String scoreMapping, ScoreRuleType ruleType, int levels) {
        super(ruleInstance, ruleType, levels);
        String[] valueAndScore = splitValueAndScore(scoreMapping);

        if (valueAndScore.length == 1) {
            // no score, auto infer value score mapping
            String[] vGroups = splitGroups(valueAndScore[0]);
            intervals = new Range[levels];
            if (vGroups.length <= 2) {
                int first = DEFAULT_2_LEVEL_HIGH_FIRST ? MAX_SCORE : MIN_SCORE;
                int second = DEFAULT_2_LEVEL_HIGH_FIRST ? MIN_SCORE : MAX_SCORE;
                boolean isExclusive = createAndAddScoreRange(first, vGroups[0]);
                if (vGroups.length == 1) {
                    if (isExclusive) {
                        throw new RuleExprInvalidException("only one range and it's exclusive match, " +
                                contextInfo(scoreMapping));
                    }
                } else {
                    createAndAddScoreRange(second, vGroups[1]);
                }
            } else {
                for (int i = levels - 1, j = vGroups.length - 1; j >= 0; --i, --j) {
                    createAndAddScoreRange(i, vGroups[j]);
                }
            }
        } else {
            String[] vGroups = splitGroups(valueAndScore[0]);
            String[] sGroups = splitGroups(valueAndScore[1]);
            if (vGroups.length != sGroups.length) {
                throw new RuleExprInvalidException("value and score group not same length, " + contextInfo(scoreMapping));
            }

            intervals = new Range[levels];
            try {
                for (int i = 0; i < sGroups.length; ++i) {
                    // only int, int.5 not considering other double
                    int score = toScoreIndex(sGroups[i]);
                    if (!getRange().contains(score)) {
                        throw new RuleExprInvalidException("score not in range, " + contextInfo(scoreMapping));
                    }
                    if (null != intervals[score]) {
                        throw new RuleExprInvalidException("duplicate score, " + contextInfo(scoreMapping));
                    }
                    createAndAddScoreRange(score, vGroups[i]);
                }
            } catch (NumberFormatException ex) {
                throw new RuleExprInvalidException("score shall be int or int.5, " + contextInfo(scoreMapping), ex);
            }
        }
    }

    /**
     * @param scoreMapping [1,5];[7,9);(11,13];(15,17);(19,)
     */
    public IntervalScoreRule(TagRuleInstance ruleInstance, String scoreMapping, ScoreRuleType ruleType) {
        this(ruleInstance, scoreMapping, ruleType, ruleType.supportP5() ? SCORE_LEVELS_H : SCORE_LEVELS);
    }

    static final Pattern BLANK_PATTERN = Pattern.compile("\\s+");
    static final Pattern RANGE_PATTERN = Pattern.compile("([\\[(])(\\d+(?:\\.\\d*)?)?,(\\d+(?:\\.\\d*)?)?([])])");

    /**
     * @param score
     * @param val
     * @return true if exclusive, otherwise normal range
     */
    private boolean createAndAddScoreRange(int score, String val) {
        if (handleExclusiveMatch(score, val)) {
            exclusiveMatchScore = score;
            return true;
        }
        Matcher matcher = BLANK_PATTERN.matcher(val);
        if (matcher.find()) {
            val = matcher.replaceAll("");
        }
        matcher = RANGE_PATTERN.matcher(val);
        if (!matcher.matches() || (null == matcher.group(2) && null == matcher.group(3))) {
            throw new RuleExprInvalidException("range must be in the form of [x,y] or (x,y] or [x,y) or (x,y) or [,y] or [x,]..." +
                    contextInfo(val));
        }
        boolean isInt = !val.contains(".");
        boolean leftOpen;
        boolean rightOpen;
        leftOpen = "(".equals(matcher.group(1));
        rightOpen = ")".equals(matcher.group(4));
        if (isInt) {
            int min = null == matcher.group(2) ? Integer.MIN_VALUE : Integer.parseInt(matcher.group(2));
            int max = null == matcher.group(3) ? Integer.MAX_VALUE : Integer.parseInt(matcher.group(3));
            intervals[score] = new IntRange(min, max, leftOpen, rightOpen);
        } else {
            double min = null == matcher.group(2) ? Long.MIN_VALUE : Double.parseDouble(matcher.group(2));
            double max = null == matcher.group(3) ? Double.MAX_VALUE : Double.parseDouble(matcher.group(3));
            intervals[score] = new DoubleRange(min, max, leftOpen, rightOpen);
        }
        return false;
    }

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

        if (null == value) {
            if (hasExclusiveMatch()) {
                return exclusiveMatchScore();
            } else {
                return null;
                /*throw new RuleExprInvalidException("interval rule eval result is null, " + contextInfo() + ", rootModel:["
                + ruleContext.getRoot().summary() + "]");*/
            }
        }
        if (!(value instanceof Number)) {
            if (hasExclusiveMatch()) {
                LOGGER.info("interval rule eval result type is " + value.getClass().getCanonicalName() +
                        ", and this rule has exclusive match, " + contextInfo() +
                        (!ruleContext.hasModel() ? "" : ", rootModel:[" + ruleContext.getRoot().summary() + "]"));
                return exclusiveMatchScore();
            } else {
                throw new RuleEvalException("interval rule eval result is not Number, " + contextInfo() +
                        (!ruleContext.hasModel() ? "" : ", rootModel:[" + ruleContext.getRoot().summary() + "]"));
            }
        } else if (value instanceof Long) {
            // just consider int part
            return getMatchScore(((Long) value).intValue());
        } else if (value instanceof Integer) {
            return getMatchScore(((Integer) value));
        } else if (value instanceof Double) {
            Double d = (Double) value;
            if (Double.isNaN(d)) {
                return hasExclusiveMatch() ? exclusiveMatchScore() : null;
            }
            return getMatchScore(d);
        } else if (value instanceof BigDecimal) {
            return getMatchScore(((BigDecimal) value).doubleValue());
        } else if (value instanceof Float) {
            return getMatchScore(((Float) value).doubleValue());
        } else {
            // throw exception for edge case
            throw new UnsupportedOperationException("interval rule eval result type is " + value.getClass().getCanonicalName() +
                    ", which is not yet supported, and this must be reported to developer to be taken in consideration."
                    + contextInfo() +
                    (!ruleContext.hasModel() ? "" : ", rootModel:[" + ruleContext.getRoot().summary() + "]"));
        }
    }

    private double getMatchScore(int calcResult) {
        for (int i = 0; i < intervals.length; ++i) {
            if (null != intervals[i] && intervals[i].contains(calcResult)) {
                return toScore(i);
            }
        }
        if (hasExclusiveMatch()) {
            return exclusiveMatchScore();
        }
        return -1;
    }

    private double getMatchScore(double calcResult) {
        for (int i = 0; i < intervals.length; ++i) {
            if (null != intervals[i] && intervals[i].contains(calcResult)) {
                return toScore(i);
            }
        }
        if (hasExclusiveMatch()) {
            return exclusiveMatchScore();
        }
        return -1;
    }
}
