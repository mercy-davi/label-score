package com.example.labelscore.service.handler;

import com.example.labelscore.bean.ScoreCardModel;
import com.example.labelscore.constants.LabelConstants;
import com.example.labelscore.core.RootModel;
import com.example.labelscore.core.RuleContext;
import com.example.labelscore.core.RuleContextPreProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Description TODO
 * @Date 2021/4/5 12:00
 * @Created by hdw
 */
@Component
public class MortgageRuleConfigPreProcessor implements RuleContextPreProcessor {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public String scoreCardCode() {
        return LabelConstants.FP_ESTATE_MORTGAGE_DEBT;
    }

    @Override
    public void process(ScoreCardModel scoreCardModel, RootModel rootModel, RuleContext ruleContext) {
        Boolean isTabInitScore = PROCESSOR_STRATEGY.get();
        logger.info("MortgageRuleConfigPreProcessor isTabInitScore:{}", isTabInitScore);
        ruleContext.putObjInContextBinding("isTabInitScore", isTabInitScore);
        PROCESSOR_STRATEGY.remove();
    }
}
