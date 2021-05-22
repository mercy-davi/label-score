package com.example.labelscore.service.handler;

import com.example.labelscore.bean.TagRuleInstance;
import com.example.labelscore.core.RuleContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.example.labelscore.service.ScoreTagConfigService.CONFIG_KEY_CONDITIONAL_IS_SUIT;

/**
 * @Description TODO
 * @Date 2021/4/5 11:01
 * @Created by hdw
 */
@Component
public class ConditionalTagIsSuitHandler {

    public void handleDef(List<TagRuleInstance> defList, RuleContext ruleContext) {
        handle(defList, ruleContext);
    }

    public void handleInstance(List<TagRuleInstance> instanceList, RuleContext ruleContext) {
        handle(instanceList, ruleContext);
    }

    private void handle(List<TagRuleInstance> list, RuleContext ruleContext) {
        Set<String> isSuitSet = ruleContext.getObjInContextBinding(CONFIG_KEY_CONDITIONAL_IS_SUIT);
        if (CollectionUtils.isEmpty(isSuitSet)) {
            return;
        }
        Iterator<TagRuleInstance> instanceIterator = list.iterator();
        while (instanceIterator.hasNext()) {
            TagRuleInstance instance = instanceIterator.next();
            if (isSuitSet.contains(instance.getDefinition().getRuleCode())) {
                instance.setSuit(false);
            }
        }
    }
}
