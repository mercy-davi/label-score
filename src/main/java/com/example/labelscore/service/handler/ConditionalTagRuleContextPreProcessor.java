package com.example.labelscore.service.handler;

import com.example.labelscore.bean.ScoreCardModel;
import com.example.labelscore.core.JSEngine;
import com.example.labelscore.core.RootModel;
import com.example.labelscore.core.RuleContext;
import com.example.labelscore.core.RuleContextPreProcessor;
import com.example.labelscore.core.RuleEvalException;
import com.example.labelscore.service.ScoreTagConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.script.ScriptException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.labelscore.core.ScoreRule.ALL_REMAINS;
import static com.example.labelscore.service.ScoreFeature.ConditionalTagExist;
import static com.example.labelscore.service.ScoreTagConfigService.CONFIG_KEY_CONDITIONAL_EXIST_TAG;
import static com.example.labelscore.service.ScoreTagConfigService.CONFIG_KEY_CONDITIONAL_EXIST_TAG_WEIGHT;

/**
 * @Description TODO
 * @Date 2021/4/5 11:08
 * @Created by hdw
 */
public class ConditionalTagRuleContextPreProcessor implements RuleContextPreProcessor {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ScoreTagConfigService scoreTagConfigService;

    @Override
    public String scoreCardCode() {
        return null;
    }

    @Override
    public void process(ScoreCardModel scoreCardModel, RootModel rootModel, RuleContext ruleContext) {
        if (scoreCardModel.scoreFeatures().isEnable(ConditionalTagExist)) {
            Map<String, String[]>[] maps =
                    scoreTagConfigService.findConditionalTagConfig(scoreCardModel.getScoreCardCode());
            if (maps.length != 0) {
                Set<String> resultSet = new HashSet<>();
                for (Map<String, String[]> map : maps) {
                    String foundKey = findKey(map, ruleContext, scoreCardModel);
                    if (null == foundKey && map.containsKey(ALL_REMAINS)) {
                        foundKey = ALL_REMAINS;
                    }
                    if (null != foundKey) {
                        String[] array = map.get(foundKey);
                        if (null != array && array.length > 0) {
                            resultSet.addAll(Arrays.asList(array));
                        }
                    } else {
                        logger.info("no item is hit by one " + CONFIG_KEY_CONDITIONAL_EXIST_TAG);
                    }
                }
                Set<String> fullSet = collect(maps);
                if (resultSet.size() != 0) {
                    logger.info("no item is hit by all " + CONFIG_KEY_CONDITIONAL_EXIST_TAG);
                }
                ruleContext.putObjInContextBinding(CONFIG_KEY_CONDITIONAL_EXIST_TAG, new Set[] {resultSet, fullSet});
            }
            Map<String, Map<String, Double>>[] weightMaps =
                    scoreTagConfigService.findConditionalTagWeightConfig(scoreCardModel.getScoreCardCode());
            if (weightMaps.length != 0) {
                Map<String, Double> resultMap = new HashMap<>();
                for (Map<String, Map<String, Double>> map : weightMaps) {
                    String foundKey = findKey(map, ruleContext, scoreCardModel);
                    if (null == foundKey && map.containsKey(ALL_REMAINS)) {
                        foundKey = ALL_REMAINS;
                    }
                    if (null != foundKey) {
                        resultMap.putAll(map.get(foundKey));
                    } else {
                        logger.info("no one is hit by " + CONFIG_KEY_CONDITIONAL_EXIST_TAG_WEIGHT);
                    }
                }
                if (resultMap.size() != 0) {
                    ruleContext.putObjInContextBinding(CONFIG_KEY_CONDITIONAL_EXIST_TAG_WEIGHT, resultMap);
                }
            }
        }
    }

    private Set<String> collect(Map<String, String[]>[] maps) {
        return Arrays.stream(maps)
                .map(Map::values).flatMap(it -> it.stream().flatMap(Arrays::stream))
                .collect(Collectors.toSet());
    }

    private String findKey(Map<String, ?> map, RuleContext ruleContext, ScoreCardModel scoreCardModel) {
        String foundKey = null;
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            try {
                Object result = JSEngine.get().eval(entry.getKey(), ruleContext.getBindings());
                if (null == result) {
                    continue;// take null as false
                }
                if (!(result instanceof Boolean)) {
                    throw new RuleEvalException("eval " + CONFIG_KEY_CONDITIONAL_EXIST_TAG + " [" + entry.getKey() + "] of scoreCard "
                    + scoreCardModel.getScoreCardCode() + " result is not boolean");
                }
                Boolean bool = (Boolean) result;
                if (null != foundKey && bool) {
                    throw new IllegalStateException("multi conditions are true: " + foundKey + ", " + entry.getKey());
                }
                if (bool) {
                    foundKey = entry.getKey();
                }
            } catch (ScriptException ex) {
                logger.info("eval " + CONFIG_KEY_CONDITIONAL_EXIST_TAG + " [" + entry.getKey() + "] of scoreCard "
                        + scoreCardModel.getScoreCardCode() + " error");
            }
        }
        return foundKey;
    }
}
