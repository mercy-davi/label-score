package com.example.score.core;

import com.example.score.bean.TagRuleInstance;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;

import static com.example.score.service.ScoreTreeLevel.PRIMARY_TAG;
import static com.example.score.service.ScoreTreeLevel.SECONDARY_TAG;

/**
 * @Description TODO
 * @Date 2021/4/4 16:27
 * @Created by hdw
 */
// todo consider whether ruleFactory is fit using here
public class SumByWeightScoreRule extends AbstractScoreRule {
    final ScoreRuleFactory ruleFactory;

    public SumByWeightScoreRule(TagRuleInstance ruleInstance, ScoreRuleFactory ruleFactory) {
        super(ruleInstance, ScoreRuleType.SumByWeight);
        this.ruleFactory = ruleFactory;
    }

    @Override
    public Double calc(RuleContext ruleContext) {
        return calculate(this.ruleInstance);
    }

    public static Double calculate(TagRuleInstance ruleInstance) {
        if (CollectionUtils.isEmpty(ruleInstance.getChildren())) {
            throw new IllegalStateException("ruleInstance has no child, " + contextInfo(ruleInstance));
        }
        if (ruleInstance.getChildren().stream().allMatch(instance -> null == instance.score())) {
            return null;
        }
        if ((ruleInstance.getLevel() == PRIMARY_TAG ||
                ruleInstance.getLevel() == SECONDARY_TAG) && !ruleInstance.isSuit()) {
            return ruleInstance.getAutoScoreValue();
        } else {
            BigDecimal scoreSum = BigDecimal.ZERO;
            for (TagRuleInstance child : ruleInstance.getChildren()) {
                scoreSum = scoreSum.add(new BigDecimal(Double.toString(child.scoreIfNullZero() * child.getDefinition().getRuleWeight())));
            }
            return scoreSum.doubleValue();
//            return ruleInstance.getChildren().stream()
//                    .mapToDouble(child -> child.scoreIfNullZero() * child.getDefinition().getRuleWeight())
//                    .sum();
        }
    }
}
