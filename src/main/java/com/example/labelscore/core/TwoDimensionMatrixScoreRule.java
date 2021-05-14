package com.example.labelscore.core;

import com.example.labelscore.bean.TagRuleInstance;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Array;

import static com.example.labelscore.core.ScoreRuleConfig.MULTI_DIMENSION_DEF_SPLIT;
import static com.example.labelscore.core.ScoreRuleConfig.MULTI_DIMENSION_MAPPING_SPLIT;
import static com.example.labelscore.core.ScoreRuleConfig.MULTI_DIMENSION_SPLIT;
import static com.example.labelscore.core.ScoreRuleConfig.SCORE_LEVELS;
import static com.example.labelscore.core.ScoreRuleConfig.SCORE_LEVELS_H;
import static com.example.labelscore.core.ScoreRuleConfig.SCORE_MAPPING_GROUP_SPLIT;
import static com.example.labelscore.core.ValueSourceExp.EXCEPTION_FALLBACK_VALUE;

/**
 * @Description TODO
 * @Date 2021/4/4 16:39
 * @Created by hdw
 */
public class TwoDimensionMatrixScoreRule extends AbstractScoreRule {

    final ScoreRuleType _1;
    final ScoreRuleType _2;

    final ExclusiveMatchScoreRule rule1;
    final ExclusiveMatchScoreRule[] rule2;

    public TwoDimensionMatrixScoreRule(TagRuleInstance ruleInstance, String scoreExp, ScoreRuleType ruleType) {
        super(ruleInstance, ScoreRuleType.TwoDimensionMatrix);
        String[] matrix = getMatrix(scoreExp);
        String scoreRuleHead = matrix[0];
        String[] head = splitSet(scoreRuleHead);
        ScoreRuleType[] types = parseHead(head);
        _1 = types[0];
        _2 = types[1];
        String[] matrixV = getMatrixVal(matrix[1]);
        String[] mappings = getMappings(matrixV[1]);
        int _1Levels = StringUtils.countMatches(matrixV[0], SCORE_MAPPING_GROUP_SPLIT) + 1;
        if (_1Levels != mappings.length) {
            throw new IllegalArgumentException("1 dimension size is not compatible with score mapping");
        }
        rule1 = ScoreRuleFactory.rule(ruleInstance, _1, matrixV[0], _1Levels);
        rule2 = new ExclusiveMatchScoreRule[mappings.length];
        for (int i = 0; i < mappings.length; ++i) {
            rule2[i] = ScoreRuleFactory.rule(ruleInstance, _2, mappings[i], ruleType.supportP5() ? SCORE_LEVELS_H : SCORE_LEVELS);
        }
    }

    private String[] getMappings(String v) {
        if (StringUtils.isEmpty(v)) {
            throw new IllegalArgumentException("mappings cannot be blank");
        }
        String[] mappings = v.split(MULTI_DIMENSION_MAPPING_SPLIT);
        trimItem(mappings);
        return mappings;
    }

    @Override
    public Double calc(RuleContext ruleContext) {
        Object val = evalForValue(ruleContext);
        if (val == EXCEPTION_FALLBACK_VALUE) {
            return null;
        }
        if (null == val) {
            return calc(null, null, ruleContext);
        } else if (val.getClass().isArray()) {
            if (Array.getLength(val) != getDimension()) {
                throw new IllegalStateException("value length is not " + getDimension());
            }
            Object val_1 = Array.get(val, 0);
            Object val_2 = Array.get(val, 1);
            return calc(val_1, val_2, ruleContext);
        } else {
            return null;
//            throw new RuleEvalException("values must be array with size of " + getDimension());
        }
    }

    private Double calc(Object val_1, Object val_2, RuleContext ruleContext) {
        Double d = rule1.calc(ruleContext, val_1);
        if (null == d) {
            return null;
        }
        int i = d.intValue();
        if (i < 0 || i >= rule2.length) {
            throw new IllegalStateException("should not be here");
        }
        return rule2[i].calc(ruleContext, val_2);
    }

    private String[] getMatrixVal(String matrix) {
        if (StringUtils.isEmpty(matrix)) {
            throw new IllegalArgumentException("matrix cannot be blank");
        }
        String[] values = matrix.split(MULTI_DIMENSION_SPLIT);
        if (values.length != getDimension()) {
            throwDimensionIncorrectException();
        }
        trimItem(values);
        return values;
    }

    private void throwDimensionIncorrectException() {
        throw new IllegalArgumentException("matrix dimension is not " + getDimension());
    }

    private ScoreRuleType[] parseHead(String[] head) {
        if (head.length <= 1) {
            throw new IllegalArgumentException("multiDimension head must contain at least two score rule type");
        }
        if (head.length != getDimension()) {
            throw new IllegalArgumentException("multiDimension head declaration is incorrect");
        }
        ScoreRuleType[] types = new ScoreRuleType[head.length];
        for (int i = 0; i < head.length; ++i) {
            types[i] = ScoreRuleType.valueOf(head[i]);
        }
        return types;
    }

    private String[] getMatrix(String scoreExp) {
        if (StringUtils.isBlank(scoreExp)) {
            throw new IllegalArgumentException("scoreExp cannot be blank");
        }
        String[] matrix = scoreExp.split(MULTI_DIMENSION_DEF_SPLIT);
        if (matrix.length != getDimension()) {
            throw new IllegalArgumentException("scoreExp matrix row is not "
                    + getDimension());
        }
        trimItem(matrix);
        return matrix;
    }

    protected int getDimension() {
        return 2;
    }
}
