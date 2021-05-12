package com.example.labelscore.core;

import com.example.labelscore.bean.ScoreBizSource;
import com.example.labelscore.bean.ScoreCardModel;
import com.example.labelscore.constants.LabelConstants;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description TODO
 * @Date 2021/4/4 15:08
 * @Created by hdw
 */
public class RuleContextFactory {

    final RootModelFactory rootModelFactory;
    final Map<String, List<RuleContextPreProcessor>> contextPreProcessor;
    final List<RuleContextPreProcessor> globalPreProcessor = new ArrayList<>();

    public RuleContextFactory(RootModelFactory rootModelFactory, List<RuleContextPreProcessor> preProcessors) {
        this.rootModelFactory = rootModelFactory;
        if (CollectionUtils.isEmpty(preProcessors)) {
            this.contextPreProcessor = Collections.emptyMap();
        } else {
            this.contextPreProcessor = new HashMap<>();
            preProcessors.forEach(preProcessor -> {
                if (null == preProcessor) {
                    return;// maybe redundant
                }
                if (null == preProcessor.scoreCardCode()) {
                    globalPreProcessor.add(preProcessor);
                } else {
                    if (!this.contextPreProcessor.containsKey(preProcessor.scoreCardCode())) {
                        this.contextPreProcessor.put(preProcessor.scoreCardCode(), new ArrayList<>());
                    }
                    this.contextPreProcessor.get(preProcessor.scoreCardCode()).add(preProcessor);
                }
            });
        }
    }

    public RuleContext create(ScoreBizSource scoreBizSource, ScoreCardModel scoreCard) {
        return create(scoreBizSource, scoreCard, false);
    }

    // 这个重载方法过于保守谨慎了，因为已经考虑懒加载了
    public RuleContext create(ScoreBizSource scoreBizSource, ScoreCardModel scoreCard, boolean forbidCreateRootModel) {
        RuleContext ruleContext;
        if (!StringUtils.isEmpty(scoreBizSource.getBizId()) && !forbidCreateRootModel) {
            // bizModel
            RootModel rootModel = rootModelFactory.createModel(scoreCard.getModelCode(), scoreBizSource.getBizId());
            ruleContext = new RuleContext(rootModel);
        } else {
            ruleContext = new RuleContext(null);
        }
        String scoreCardCode = scoreCard.getScoreCardCode();
        // 便于自动取值时针对投前和投后做不同的逻辑处理
        if (scoreCardCode.equals(LabelConstants.AFTER_COMMON_LABEL_EVALUATION) || scoreCardCode.equals(LabelConstants.AFTER_ESTATE_MORTGAGE_DEBT)
                || scoreCardCode.equals(LabelConstants.AFTER_ESTATE_CREDIT_DEBT) || scoreCardCode.equals(LabelConstants.AFTER_GOVERNMENT_FINANCING)) {
            ruleContext.putObjInContextBinding("after", LabelConstants.AFTER_LABEL_SCORE);
        }
        List<RuleContextPreProcessor> localPreProcessors = this.contextPreProcessor.get(scoreCard.getScoreCardCode());
        if (!CollectionUtils.isEmpty(localPreProcessors)) {
            localPreProcessors.forEach(ruleContextPreProcessor ->
                    ruleContextPreProcessor.process(scoreCard, ruleContext.getRoot(), ruleContext)
            );
        }
        globalPreProcessor.forEach(ruleContextPreProcessor ->
                ruleContextPreProcessor.process(scoreCard, ruleContext.getRoot(), ruleContext)
        );

        return ruleContext;
    }


}
