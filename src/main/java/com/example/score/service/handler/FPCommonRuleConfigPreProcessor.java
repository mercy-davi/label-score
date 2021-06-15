package com.example.score.service.handler;

import com.example.score.bean.ScoreCardModel;
import com.example.score.constants.LabelConstants;
import com.example.score.core.RootModel;
import com.example.score.core.RuleContext;
import com.example.score.core.RuleContextPreProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Description TODO
 * @Date 2021/4/5 12:00
 * @Created by hdw
 */
@Component
public class FPCommonRuleConfigPreProcessor implements RuleContextPreProcessor {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public String scoreCardCode() {
        return LabelConstants.FP_THREE_LEVEL_LABEL_EVALUATION;
    }

    @Override
    public void process(ScoreCardModel scoreCardModel, RootModel rootModel, RuleContext ruleContext) {
        Boolean isTabInitScore = PROCESSOR_STRATEGY.get();
        logger.info("FPCommonRuleConfigPreProcessor isTabInitScore:{}", isTabInitScore);
        ruleContext.putObjInContextBinding("isTabInitScore", isTabInitScore);
        PROCESSOR_STRATEGY.remove();
    }
}
