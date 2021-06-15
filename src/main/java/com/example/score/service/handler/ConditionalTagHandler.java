package com.example.score.service.handler;

import com.example.score.bean.TagRuleDef;
import com.example.score.bean.TagRuleInstance;
import com.example.score.core.RuleContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.example.score.service.ScoreTagConfigService.CONFIG_KEY_CONDITIONAL_EXIST_TAG;

/**
 * @Description TODO
 * @Date 2021/4/5 10:32
 * @Created by hdw
 */
@Component
public class ConditionalTagHandler {

    /**
     * This method is used for exclusion semantics in configuration.
     * In that way instance tables is inserted with full collection,
     * and the ignored tags are handled(removed) before returning to caller.
     *
     * But now, instances table is inserted with the real conditional tags, no more and of course no less.
     * So, this method is outdated and incorrect.
     *
     * @deprecated in favor of {@link #handleDef4Exclude(List, RuleContext)} ()}
     */
    @Deprecated
    public void handleInstance4exclude(List<TagRuleInstance> list, RuleContext ruleContext) {
        Set<String> excludeList = ruleContext.getObjInContextBinding(CONFIG_KEY_CONDITIONAL_EXIST_TAG);
        if (CollectionUtils.isEmpty(excludeList)) {
            return;
        }
        Iterator<TagRuleInstance> instanceIterator = list.iterator();
        while (instanceIterator.hasNext()) {
            TagRuleInstance item = instanceIterator.next();
            String ruleCode = item.getDefinition().getRuleCode();
            if (excludeList.contains(ruleCode)) {
//                instanceIterator.remove();
                item.removeFromTree();
            }
        }
    }

    public void handleDef4Exclude(List<TagRuleDef> list, RuleContext ruleContext) {
        if (!ruleContext.containsObjInContextBinding(CONFIG_KEY_CONDITIONAL_EXIST_TAG)) {
            return;
        }
        Set<String>[] setArray = ruleContext.getObjInContextBinding(CONFIG_KEY_CONDITIONAL_EXIST_TAG);
        Set<String> include = setArray[0];
        Set<String> full = setArray[1];
        if (full.isEmpty()) {
            return;
        }
        Iterator<TagRuleDef> defIterator = list.iterator();
        while (defIterator.hasNext()) {
            TagRuleDef def = defIterator.next();
            if (full.contains(def.getRuleCode()) && !include.contains(def.getRuleCode())) {
                defIterator.remove();;
                def.removeFromTree();
            }
        }
    }
}
