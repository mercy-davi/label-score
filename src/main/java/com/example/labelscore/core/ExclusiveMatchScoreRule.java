package com.example.labelscore.core;

import com.example.labelscore.bean.TagRuleInstance;
import com.example.labelscore.util.Range;
import org.apache.commons.lang.StringUtils;

import static com.example.labelscore.core.ScoreRuleConfig.P5;
import static com.example.labelscore.core.ScoreRuleConfig.SCORE_RANGE;
import static com.example.labelscore.core.ScoreRuleConfig.SCORE_RANGE_H;

/**
 * @Description TODO
 * @Date 2021/4/3 20:58
 * @Created by hdw
 */
abstract class ExclusiveMatchScoreRule extends AbstractScoreRule {
    int exclusiveMatchScore = NON_EXCLUSIVE_MATCH_SCORE;
    final int levels;

    ExclusiveMatchScoreRule(TagRuleInstance ruleInstance, ScoreRuleType scoreRuleType) {
        this(ruleInstance, scoreRuleType, ScoreRuleConfig.SCORE_LEVELS);
    }

    ExclusiveMatchScoreRule(TagRuleInstance ruleInstance, ScoreRuleType scoreRuleType, int levels) {
        super(ruleInstance, scoreRuleType);
        this.levels = levels;
    }

    boolean handleExclusiveMatch(int score, String val) {
        if (isAllRemains(val)) {
            if (exclusiveMatchScore != NON_EXCLUSIVE_MATCH_SCORE) {
                throw new RuleExprInvalidException("cannot define multiple exclusive match score values, " +
                        contextInfo());
            }
            exclusiveMatchScore = score;
            return true;
        }
        return false;
    }

    String[] splitGroups(String scoreMapping) {
        if (StringUtils.isEmpty(scoreMapping)) {
            throw new RuleExprInvalidException("value or score cannot be null or empty");
        }
        String[] groups = scoreMapping.split(ScoreRuleConfig.SCORE_MAPPING_GROUP_SPLIT);
        if (groups.length > levels) {
            throw new RuleExprInvalidException("groups length is more than the score levels [" +
                    scoreMapping + "]");
        }
        trimItem(groups);
        return groups;
    }

    @Override
    public double exclusiveMatchScore() {
        return toScore(exclusiveMatchScore);
    }

    abstract Double calc(RuleContext ruleContext, Object value);

    protected int toScoreIndex(String s) {
        boolean isP5 = s.contains(P5);
        int score = !isP5 ? Integer.parseInt(s) :
                handleScoreRuleType.supportP5() ? (int) Double.parseDouble(s) : -1;
        if (score == -1) {
            throw new RuleExprInvalidException("score shall be int for " + handleScoreRuleType +
                    "not supporting P5");
        }
        if (handleScoreRuleType.supportP5()) {
            score *= 2;
        }
        if (isP5) {
            ++score;
        }
        return score;
    }

    protected double toScore(int scoreIndex) {
        return !this.handleScoreRuleType.supportP5() ? scoreIndex :
                (scoreIndex & 1) == 0 ? scoreIndex / 2 : scoreIndex / 2d;
    }

    protected Range getRange() {
        return this.handleScoreRuleType.supportP5() ? SCORE_RANGE_H : SCORE_RANGE;
    }
}
