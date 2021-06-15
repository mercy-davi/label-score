package com.example.score.service.handler;

import com.example.score.bean.HierarchyRecord;
import com.example.score.bean.TagRuleDef;
import com.example.score.bean.TagRuleInstance;
import com.example.score.core.RuleContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.example.score.service.ScoreTagConfigService.CONFIG_KEY_CONDITIONAL_EXIST_TAG_WEIGHT;

/**
 * @Description TODO
 * @Date 2021/4/5 11:52
 * @Created by hdw
 */
@Component
public class ConditionalTagWeightHandler {

    public void handleDef(List<TagRuleDef> defList, RuleContext ruleContext) {
        handle(defList, ruleContext, Function.identity());
    }

    public void handleInstance(List<TagRuleInstance> instanceList, RuleContext ruleContext) {
        handle(instanceList, ruleContext, TagRuleInstance::getDefinition);
    }

    private <T extends HierarchyRecord<?>> void handle(List<T> list, RuleContext ruleContext,
                                                       Function<T, TagRuleDef> ruleDefExtractor) {
        Map<String, Double> weightMaps = ruleContext.getObjInContextBinding(CONFIG_KEY_CONDITIONAL_EXIST_TAG_WEIGHT);
        if (CollectionUtils.isEmpty(weightMaps)) {
            return;
        }
        list.forEach(item -> {
            TagRuleDef ruleDef = ruleDefExtractor.apply(item);
            if (weightMaps.containsKey(ruleDef.getRuleCode())) {
                ruleDef.setRuleWeight(weightMaps.get(ruleDef.getRuleCode())); // may clone instead
            }
        });
    }
}
