package com.example.labelscore.core;

import com.example.labelscore.bean.ScoreCardModel;

/**
 * @Description TODO
 * @Date 2021/4/4 15:41
 * @Created by hdw
 */
public interface RuleContextPreProcessor {
    ThreadLocal<Boolean> PROCESSOR_STRATEGY = new ThreadLocal<>();
    String scoreCardCode();
    void process(ScoreCardModel scoreCardModel, RootModel rootModel, RuleContext ruleContext);
}
