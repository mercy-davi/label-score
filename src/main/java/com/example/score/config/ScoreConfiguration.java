package com.example.score.config;

import com.example.score.constants.LabelConstants;
import com.example.score.constants.LabelConstantsFPTrust;
import com.example.score.core.RootModel;
import com.example.score.core.RootModelFactory;
import com.example.score.core.RootModelPreProcessor;
import com.example.score.core.RootModelSupplier;
import com.example.score.core.RuleContextFactory;
import com.example.score.core.RuleContextPreProcessor;
import com.example.score.core.ScoreRuleFactory;
import com.example.score.core.ValueScoreStrategy;
import com.example.score.service.ContextStrMaker;
import com.example.score.service.DefaultContextStrMaker;
import com.example.score.service.FPDefaultValueScoreStrategy;
import com.example.score.service.ScoreDefService;
import com.example.score.service.ScoreInstanceService;
import com.example.score.service.handler.ConditionalTagRuleContextPreProcessor;
import com.example.score.service.impl.ScoreDefServiceImpl;
import com.example.score.service.impl.ScoreInstanceServiceImpl;
import com.example.score.util.ConfigUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import java.util.List;

import static com.example.score.core.ValueScoreStrategy.DEFAULT_NAME;

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
