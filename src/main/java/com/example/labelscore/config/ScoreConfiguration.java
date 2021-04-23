package com.example.labelscore.config;

import com.example.labelscore.constants.LabelConstants;
import com.example.labelscore.constants.LabelConstantsFPTrust;
import com.example.labelscore.core.RootModel;
import com.example.labelscore.core.RootModelFactory;
import com.example.labelscore.core.RootModelPreProcessor;
import com.example.labelscore.core.RootModelSupplier;
import com.example.labelscore.core.RuleContextFactory;
import com.example.labelscore.core.RuleContextPreProcessor;
import com.example.labelscore.core.ScoreRuleFactory;
import com.example.labelscore.core.ValueScoreStrategy;
import com.example.labelscore.service.ContextStrMaker;
import com.example.labelscore.service.DefaultContextStrMaker;
import com.example.labelscore.service.FPDefaultValueScoreStrategy;
import com.example.labelscore.service.ScoreDefService;
import com.example.labelscore.service.ScoreInstanceService;
import com.example.labelscore.service.handler.ConditionalTagRuleContextPreProcessor;
import com.example.labelscore.service.impl.ScoreDefServiceImpl;
import com.example.labelscore.service.impl.ScoreInstanceServiceImpl;
import com.example.labelscore.util.ConfigUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import java.util.List;

import static com.example.labelscore.core.ValueScoreStrategy.DEFAULT_NAME;

/**
 * @Description TODO
 * @Date 2021/4/3 11:17
 * @Created by hdw
 */
@Configuration
public class ScoreConfiguration {
    @Autowired
    ConfigUtil configUtil;

    @Autowired(required = false)
    List<RootModelSupplier<? extends RootModel>> rootModelSuppliers;
    @Autowired(required = false)
    List<RootModelPreProcessor<? extends RootModel>> rootModelPreProcessors;

    @Bean
    @ConditionalOnMissingBean
    public ScoreDefService scoreDefService() {
        return new ScoreDefServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public ScoreInstanceService scoreInstanceService() {
        return new ScoreInstanceServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public ContextStrMaker contextStrMaker() {
        return new DefaultContextStrMaker();
    }

    @Bean
    public RootModelFactory rootModelFactory(EntityManager entityManager) {
        return new RootModelFactory(entityManager, rootModelSuppliers, rootModelPreProcessors);
    }

    @Bean
    public RuleContextFactory ruleContextFactory(
            RootModelFactory rootModelFactory,
            @Autowired(required = false) List<RuleContextPreProcessor> preProcessors) {
        return new RuleContextFactory(rootModelFactory, preProcessors);
    }

    @Bean
    public ScoreRuleFactory scoreRuleFactory() {
        return new ScoreRuleFactory();
    }

    @Bean
    @ConditionalOnMissingBean(name = DEFAULT_NAME)
    public ValueScoreStrategy defaultValueScoreStrategy() {
        return new FPDefaultValueScoreStrategy();
    }

    @Bean
    public LabelConstants labelConstants() {
        return new LabelConstantsFPTrust();
    }

    @Bean
    public RuleContextPreProcessor conditionalTagRuleContextPreProcessor() {
        return new ConditionalTagRuleContextPreProcessor();
    }
}
