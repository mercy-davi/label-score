package com.example.labelscore.service.impl;

import com.example.labelscore.bean.HierarchyRecord;
import com.example.labelscore.bean.MiscItemBean;
import com.example.labelscore.bean.MiscMetaBean;
import com.example.labelscore.bean.ScoreBizSource;
import com.example.labelscore.bean.ScoreCardModel;
import com.example.labelscore.bean.TagRuleDef;
import com.example.labelscore.bean.TagRuleInstance;
import com.example.labelscore.bean.TopTagRuleInstance;
import com.example.labelscore.constants.LabelConstants;
import com.example.labelscore.core.RootModel;
import com.example.labelscore.core.RuleContext;
import com.example.labelscore.core.RuleContextFactory;
import com.example.labelscore.core.ScoreRuleFactory;
import com.example.labelscore.core.SumByWeightScoreRule;
import com.example.labelscore.core.ValueScoreStrategy;
import com.example.labelscore.core.ValueSourceExp;
import com.example.labelscore.dao.ScoreCardModelDao;
import com.example.labelscore.dao.ScoreRuleInstanceDao;
import com.example.labelscore.entity.AfterProjApply;
import com.example.labelscore.entity.ProjApply;
import com.example.labelscore.service.InputInCompleteException;
import com.example.labelscore.service.ScoreDefService;
import com.example.labelscore.service.ScoreInstanceService;
import com.example.labelscore.service.ScoreTagConfigService;
import com.example.labelscore.service.handler.ConditionalTagHandler;
import com.example.labelscore.service.handler.ConditionalTagIsSuitHandler;
import com.example.labelscore.service.handler.ConditionalTagWeightHandler;
import com.example.labelscore.util.RequestUtil;
import com.example.labelscore.util.ScoreCommonUtil;
import com.example.labelscore.util.Tuple2;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.example.labelscore.bean.HierarchyRecord.flat;
import static com.example.labelscore.core.ScoreRuleType.SumByWeight;
import static com.example.labelscore.core.ValueScoreStrategy.CURRENT_USER;
import static com.example.labelscore.core.ValueScoreStrategy.DEFAULT_NAME;
import static com.example.labelscore.core.ValueScoreStrategy.STRATEGY;
import static com.example.labelscore.core.ValueSourceExp.toStr;
import static com.example.labelscore.service.ScoreFeature.ConditionalTagExist;
import static com.example.labelscore.service.ScoreTagConfigService.DEFAULT_VALUE_CREDIT_RECORD;
import static com.example.labelscore.service.ScoreTagConfigService.DEFAULT_VALUE_SENTIMENT;
import static com.example.labelscore.service.ScoreTreeLevel.PRIMARY_TAG;
import static com.example.labelscore.service.ScoreTreeLevel.SECONDARY_TAG;
import static com.example.labelscore.service.ScoreTreeLevel.TERTIARY_TAG;
import static com.example.labelscore.service.ScoreTreeLevel.TREE_ROOT;

/**
 * @Description TODO
 * @Date 2021/4/5 16:08
 * @Created by hdw
 */
//todo ScoreInstance 没必要主键连续自然数
public class ScoreInstanceServiceImpl implements ScoreInstanceService {

    private static final double SCORE_PRECISION = 0.05;
    private static final String NOT_EXIST = " not exist";
    private static final String SCORE_BIZ_SRC_ID = "scoreBizSrcId";

    @Autowired
    ScoreRuleInstanceDao scoreRuleInstanceDao;

    @Autowired
    ScoreRuleFactory scoreRuleFactory;

    @Autowired
    ScoreCardModelDao scoreCardModelDao;

    @Autowired
    ScoreDefService scoreDefService;

    @Autowired
    RuleContextFactory ruleContextFactory;

    @Autowired
    @Qualifier(DEFAULT_NAME)
    ValueScoreStrategy defaultStrategy;

    @Autowired
    ScoreTagConfigService scoreTagConfigService;


    /**
     * handlers for ScoreTagConfig
     */
    final ConditionalTagHandler conditionalTagHandler = new ConditionalTagHandler();
    final ConditionalTagWeightHandler conditionalTagWeightHandler = new ConditionalTagWeightHandler();
    final ConditionalTagIsSuitHandler conditionalTagIsSuitHandler = new ConditionalTagIsSuitHandler();

    private ValueScoreStrategy getStrategy() {
        ValueScoreStrategy strategy = STRATEGY.get();
        return null == strategy ? defaultStrategy : strategy;
    }

    private void removeStrategy() {
        STRATEGY.remove();
    }

    private ScoreBizSource findScoreBizSourceOrThrowException(Long scoreBizSrcId) {
        if (null == scoreBizSrcId) {
            throw new IllegalArgumentException("scoreBizSrcId cannot be null");
        }
        ScoreBizSource scoreBizSource = scoreRuleInstanceDao.findBizSrcByIdWithScoreCard(scoreBizSrcId);
        if (null == scoreBizSource) {
            throw new IllegalArgumentException("scoreBizSrcId: " + scoreBizSrcId + NOT_EXIST);
        }
        return scoreBizSource;
    }

    private ScoreBizSource findScoreBizSourceOrThrowException(String scoreCardCode, String context) {
        ScoreBizSource scoreBizSource = findScoreBizSource(scoreCardCode, context);
        if (null == scoreBizSource) {
            throw new IllegalArgumentException("scoreCardCode: " + scoreCardCode + ", context" + context + NOT_EXIST);
        }
        return scoreBizSource;
    }

    private ScoreBizSource findScoreBizSource(String scoreCardCode, String context) {
        if (StringUtils.isEmpty(scoreCardCode) || StringUtils.isEmpty(context)) {
            throw new IllegalArgumentException("scoreCardCode, context cannot be null");
        }
        return scoreRuleInstanceDao.findBizSrcByCodeWithScoreCard(scoreCardCode, context);
    }

    private ScoreBizSource findScoreBizSourceByCardId(String scoreCardId, String context) {
        if (StringUtils.isEmpty(scoreCardId) || StringUtils.isEmpty(context)) {
            throw new IllegalArgumentException("scoreCardId, context cannot be null");
        }
        return scoreRuleInstanceDao.findBizSrc(scoreCardId, context);
    }

    private ScoreBizSource findScoreBizSourceByCardIdOrThrowException(String scoreCardId, String context) {
        ScoreBizSource scoreBizSource = findScoreBizSourceByCardId(scoreCardId, context);
        if (null == scoreBizSource) {
            throw new IllegalArgumentException("scoreCardId: " + scoreCardId + ", context" + context + NOT_EXIST);
        }
        return scoreBizSource;
    }

    private ScoreCardModel findScoreCardOrThrowException(String card, CardType cardType) {
        ScoreCardModel scoreCardModel = cardType == CardType.scoreCardCode ? scoreCardModelDao.findScoreCardModel(card)
                : scoreCardModelDao.findScoreCardModelById(card);
        if (null == scoreCardModel) {
            throw new IllegalArgumentException("score card " + cardType + ": " + card + "does not exist");
        }
        return scoreCardModel;
    }

    @Override
    public ScoreBizSource searchBizSourceByContext(String context, String scoreCardCode) {
        if (StringUtils.isBlank(context) || StringUtils.isBlank(scoreCardCode)) {
            throw new IllegalArgumentException("context, scoreCardCode cannot be null");
        }
//        ScoreBizSource scoreBizSource = scoreRuleInstanceDao.findBizSrcByCode(scoreCardCode, context);
//        if (null != scoreBizSource) {
//            return scoreBizSource;
//        }
//        return null;
        return scoreRuleInstanceDao.findBizSrcByCode(scoreCardCode, context);
    }

    @Override
    public List<ScoreBizSource> searchContextLike(String contextLike, String scoreCardCode) {
        if (StringUtils.isBlank(contextLike)) {
            throw new IllegalArgumentException("contextLike cannot be blank");
        }
        if (StringUtils.isBlank(scoreCardCode)) {
            scoreCardCode = null;
        }
        return scoreRuleInstanceDao.findAllLikeByCode(contextLike, scoreCardCode);
    }

    @Override
    public List<ScoreBizSource> searchContextLikeById(String contextLike, String scoreCardId) {
        if (StringUtils.isBlank(contextLike)) {
            throw new IllegalArgumentException("contextLike cannot be blank");
        }
        if (StringUtils.isBlank(scoreCardId)) {
            scoreCardId = null;
        }
        return scoreRuleInstanceDao.findAllLike(contextLike, scoreCardId);
    }

    @Override
    public ScoreBizSource findFirstFromTail2Top(String scoreCardCode, String[] contexts) {
        return findFirstFromTail2Top(scoreCardCode, CardType.scoreCardCode, contexts);
    }

    @Override
    public ScoreBizSource findFirstFromTail2TopByCardId(String scoreCardId, String[] contexts) {
        return findFirstFromTail2Top(scoreCardId, CardType.scoreCardId, contexts);
    }

    private ScoreBizSource findFirstFromTail2Top(String card, CardType cardType, String[] contexts) {
//        if (StringUtils.isBlank(card)) throw new IllegalArgumentException(cardType + " cannot be blank");
//        if (null == contexts || contexts.length == 0) throw new IllegalArgumentException("contexts cannot be empty");
        if (contexts != null && contexts.length > 0) {
            for (int i = contexts.length - 1; i >= 0; --i) {
                ScoreBizSource scoreBizSource = cardType == CardType.scoreCardCode ?
                        scoreRuleInstanceDao.findBizSrcByCode(card, contexts[i]) :
                        scoreRuleInstanceDao.finBizSrc(card, contexts[i]);
                if (null != scoreBizSource) {
                    return scoreBizSource;
                }
            }
        }
        return null;
    }

    @Override
    public boolean isLastSaveSubmit(Long scoreBizSrcId) {
        Boolean queryResult = scoreRuleInstanceDao.getScoreBizSrcSaveState(scoreBizSrcId);
        return null != queryResult && queryResult;
    }

    @Override
    public boolean isLastSaveSubmit(String scoreCardCode, String context) {
        Boolean queryResult = scoreRuleInstanceDao.getScoreBizSrcSaveStateByCardCode(scoreCardCode, context);
        return null != queryResult && queryResult;
    }

    @Override
    public boolean isLastSaveSubmitByCardId(String scoreCardId, String context) {
        Boolean queryResult = scoreRuleInstanceDao.getScoreBizSrcSaveStateByCardId(scoreCardId, context);
        return null != queryResult && queryResult;
    }

    @Override
    public TopTagRuleInstance viewSolid(Long scoreBizSrcId, int maxScoreTreeLevel) {
        try {
            ScoreBizSource scoreBizSource = findScoreBizSourceOrThrowException(scoreBizSrcId);
            return prepareTopTagRuleInstance(scoreBizSource, maxScoreTreeLevel, true);
        } finally {
            removeStrategy();
        }
    }

    private TopTagRuleInstance prepareTopTagRuleInstance(ScoreBizSource scoreBizSource, int maxScoreTreeLevel, boolean readOnly) {
        List<TagRuleInstance> list = scoreRuleInstanceDao.findByBizSrcIdAndLevel(scoreBizSource.getId(), maxScoreTreeLevel);
        if (CollectionUtils.isEmpty(list)) {
            throw new IllegalArgumentException("scoreBizSrcId: " + scoreBizSource.getId() + ", maxScoreTreeLevel: "
                    + maxScoreTreeLevel + ", no records found");
        }
        TopTagRuleInstance top = prepareTopTagRuleInstance(scoreBizSource, list, readOnly).setMaxDepth(maxScoreTreeLevel);
        applyFEState(list, getStrategy());
        return top;
    }

    private TopTagRuleInstance prepareTopTagRuleInstance(ScoreBizSource scoreBizSource, List<TagRuleInstance> list, boolean readOnly) {
        int maxLevel = HierarchyRecord.buildTree(list);
        TopTagRuleInstance top = TopTagRuleInstance.from(list.get(0))
                .setScoreBizSource(scoreBizSource)
                .setMaxDepth(maxLevel)
                .setItemCount(list.size())
                .setReadOnly(readOnly);
        list.set(0, top);
        top.getChildren().forEach(child -> child.justSetParent(top));
        return top;
    }

    private TopTagRuleInstance prepareTopTagRuleInstance(ScoreBizSource scoreBizSource, boolean readOnly) {
        List<TagRuleInstance> list = scoreRuleInstanceDao.findByBizSrcIdWithDef(scoreBizSource.getId());
        if (CollectionUtils.isEmpty(list)) {
            throw new IllegalArgumentException("scoreBizSrcId: " + scoreBizSource.getId() + ", no records found");
        }
        TopTagRuleInstance top = prepareTopTagRuleInstance(scoreBizSource, list, readOnly);
        applyFEState(list, getStrategy());
        return top;
    }

    @Override
    public TopTagRuleInstance viewSolid(String scoreCardCode, String context, int maxScoreTreeLevel) {
        try {
            ScoreBizSource scoreBizSource = findScoreBizSourceOrThrowException(scoreCardCode, context);
            return prepareTopTagRuleInstance(scoreBizSource, maxScoreTreeLevel, true);
        } finally {
            removeStrategy();
        }
    }

    @Override
    public TopTagRuleInstance viewSolidByCardId(String scoreCardId, String context, int maxScoreTreeLevel) {
        try {
            ScoreBizSource scoreBizSource = findScoreBizSourceByCardIdOrThrowException(scoreCardId, context);
            return prepareTopTagRuleInstance(scoreBizSource, maxScoreTreeLevel, true);
        } finally {
            removeStrategy();
        }
    }

    @Override
    public TopTagRuleInstance viewSolidComplete(Long scoreBizSrcId) {
        try {
            ScoreBizSource scoreBizSource = findScoreBizSourceOrThrowException(scoreBizSrcId);
            return prepareTopTagRuleInstance(scoreBizSource, true);
        } finally {
            removeStrategy();
        }
    }

    @Override
    public TopTagRuleInstance viewSolidComplete(String scoreCardCode, String context) {
        try {
            ScoreBizSource scoreBizSource = findScoreBizSourceOrThrowException(scoreCardCode, context);
            return prepareTopTagRuleInstance(scoreBizSource, true);
        } finally {
            removeStrategy();
        }
    }

    @Override
    public TopTagRuleInstance viewSolidCompleteByCardId(String scoreCardId, String context) {
        try {
            ScoreBizSource scoreBizSource = findScoreBizSourceByCardIdOrThrowException(scoreCardId, context);
            return prepareTopTagRuleInstance(scoreBizSource, true);
        } finally {
            removeStrategy();
        }
    }

    @Override
    public TopTagRuleInstance viewSolidAverage(Object[][] averageSources, int maxScoreTreeLevel, boolean canAvgEmpty) {
        if (null == averageSources || averageSources.length == 0) {
            throw new IllegalArgumentException("averageSources cannot be empty");
        }
        if (null == averageSources[0] || averageSources[0].length == 0) {
            throw new IllegalArgumentException("averageSources[0] is columnNames, and cannot be empty");
        }
        if (averageSources[0].length + 1 != averageSources.length) {
            throw new IllegalArgumentException("averageSources.length must be equal to 1 + averageSources[0].length");
        }
        if (!(averageSources[0] instanceof String[])) {
            throw new IllegalArgumentException("averageSources[0] should be of type String[]");
        }
        String[] heads = (String[]) averageSources[0];
        for (int i = 1; i < averageSources.length; ++i) {
            if (!(averageSources[i] instanceof Long[])) {
                throw new IllegalArgumentException("averageSources[i>=0] should be type of Long[]");
            }
        }
        Long randomScoreBizSrc = getNotNullRandomBizSrcId(averageSources);
        ScoreBizSource scoreBizSource = findScoreBizSourceOrThrowException(randomScoreBizSrc);

        List<TagRuleInstance> list = initInstanceList(scoreBizSource, maxScoreTreeLevel, true).get_2();

        TopTagRuleInstance top = prepareTopTagRuleInstance(scoreBizSource, list, true);
        top.setEmpty(true);
        attachMiscBean(top, heads, list);
        int listSize = list.size();
        for (int i = 1; i < averageSources.length; ++i) {
            if (null == averageSources[i] && canAvgEmpty) {
                continue;
            }
            List<Double> avgScores = scoreRuleInstanceDao.findAverage((Long[]) averageSources[i], maxScoreTreeLevel);
            if (CollectionUtils.isEmpty(avgScores)) {
                throw new IllegalArgumentException("averageSources: " + Arrays.toString(averageSources[i]) + ", no records");
            }
            if (avgScores.size() != listSize) {
                throw new UnsupportedOperationException("avg score size is not consistent among averageSources, maybe definition changed, "
                        + "averageSources: " + Arrays.toString(averageSources[i]));
            }
            for (int j = 1; j < listSize; ++j) {
                ((MiscItemBean) list.get(j).getExtraProp()).avgScores[i - 1] = avgScores.get(j);
            }
        }
        applyFEState(list, getStrategy());
        return top;
    }

    @Override
    public TopTagRuleInstance viewSolidAverage(String scoreCardCode, String[][] averageSources, int maxScoreTreeLevel, boolean canAvgEmpty) {
        return viewSolidAverage(scoreCardCode, averageSources, maxScoreTreeLevel, CardType.scoreCardCode, canAvgEmpty);
    }

    @Override
    public TopTagRuleInstance viewSolidAverageByCardId(String scoreCardId, String[][] averageSources, int maxScoreTreeLevel, boolean canAvgEmpty) {
        return viewSolidAverage(scoreCardId, averageSources, maxScoreTreeLevel, CardType.scoreCardId, canAvgEmpty);
    }

    private TopTagRuleInstance viewSolidAverage(String card, String[][] averageSources, int maxScoreTreeLevel, CardType cardType, boolean canAvgEmpty) {
        if (StringUtils.isEmpty(card)) {
            throw new IllegalArgumentException(cardType + " cannot be empty");
        }
        if (null == averageSources || averageSources.length == 0) {
            throw new IllegalArgumentException("averageSources cannot be empty");
        }
        if (averageSources[0].length + 1 != averageSources.length) {
            throw new IllegalArgumentException("averageSources.length must be equal to 1 + averageSources[0].length");
        }
        String randomContext = getNotNullRandomContext(averageSources);
        ScoreBizSource scoreBizSource = cardType == CardType.scoreCardCode ?
                findScoreBizSourceOrThrowException(card, randomContext) :
                findScoreBizSourceByCardIdOrThrowException(card, randomContext);

        List<TagRuleInstance> list = initInstanceList(scoreBizSource, maxScoreTreeLevel, true).get_2();

        TopTagRuleInstance top = prepareTopTagRuleInstance(scoreBizSource, list, true);
        top.setMaxDepth(maxScoreTreeLevel); // maybe redundant
        top.setEmpty(true);
        attachMiscBean(top, averageSources[0], list);

        int listSize = list.size();
        for (int i = 1; i < averageSources.length; ++i) {
            if (null == averageSources[i] && canAvgEmpty) {
                continue;
            }
            List<Double> avgScores = cardType == CardType.scoreCardCode ?
                    scoreRuleInstanceDao.findAverageByOneCardCode(card, averageSources[i], maxScoreTreeLevel) :
                    scoreRuleInstanceDao.findAverageByOneCardId(card, averageSources[i], maxScoreTreeLevel);
            if (CollectionUtils.isEmpty(avgScores)) {
                // throw new IllegalArgumentException("averageSources: " + Arrays.toString(averageSources[i]) + ", no records");
                continue;
            }
            if (avgScores.size() != listSize) {
                throw new UnsupportedOperationException("avg score size is not equal to current record, maybe definition changed, "
                        + "current" + cardType + ": " + card + ", averageSources: "
                        + Arrays.toString(averageSources[i]));
            }
            for (int j = 1; j < listSize; ++j) {
                ((MiscItemBean) list.get(j).getExtraProp()).avgScores[i - 1] = avgScores.get(j);
            }
        }
        applyFEState(list, getStrategy());
        return top;
    }

    private String getNotNullRandomContext(String[][] averageSources) {
        for (int i = 1; i < averageSources.length; ++i) {
            if (null == averageSources[i] || averageSources[i].length == 0) {
                continue;
            }
            for (String context : averageSources[i]) {
                if (!StringUtils.isBlank(context)) {
                    return context;
                }
            }
        }
        throw new IllegalArgumentException("all context are blank");
    }

    private Long getNotNullRandomBizSrcId(Object[][] averageSources) {
        for (int i = 1; i < averageSources.length; ++i) {
            if (null == averageSources[i] || averageSources[i].length == 0) {
                continue;
            }
            Long[] bizSrcIds = (Long[]) averageSources[i];
            for (Long id : bizSrcIds) {
                if (null != id) {
                    return id;
                }
            }
        }
        throw new IllegalArgumentException("all bizSrcIds are empty");
    }

    private void applyFEState(List<TagRuleInstance> list, ValueScoreStrategy strategy) {
        list.forEach(instance -> instance.setRwState(strategy.fe_state(instance.getDefinition())));
    }

    @Override
    public TopTagRuleInstance viewEmpty(String scoreCardCode, String[][] averageSources, int maxScoreTreeLevel, boolean canAvgEmpty) {
        return viewEmpty(scoreCardCode, CardType.scoreCardCode, averageSources, maxScoreTreeLevel, canAvgEmpty);
    }

    @Override
    public TopTagRuleInstance viewEmptyByCardId(String scoreCardId, String[][] averageSources, int maxScoreTreeLevel, boolean canAvgEmpty) {
        return viewEmpty(scoreCardId, CardType.scoreCardId, averageSources, maxScoreTreeLevel, canAvgEmpty);
    }

    private TopTagRuleInstance viewEmpty(String card, CardType cardType, String[][] averageSources, int maxScoreTreeLevel, boolean canAvgEmpty) {
        if (StringUtils.isEmpty(card)) {
            throw new IllegalArgumentException(cardType + "cannot be empty");
        }

        ScoreCardModel scoreCardModel = findScoreCardOrThrowException(card, cardType);
        ScoreBizSource emptyScoreBizSrc = new ScoreBizSource(null, null, scoreCardModel);
        emptyScoreBizSrc.setId(-1L);
        List<TagRuleInstance> list = initInstanceList(emptyScoreBizSrc, maxScoreTreeLevel, true).get_2();
        TopTagRuleInstance top = prepareTopTagRuleInstance(emptyScoreBizSrc, list, true);
        top.setMaxDepth(maxScoreTreeLevel); // maybe redundant
        top.setEmpty(true);
        if (null != averageSources) {
            if (averageSources[0].length + 1 != averageSources.length) {
                throw new IllegalArgumentException("averageSources.length must be equal to 1 + averageSources[0].length");
            }
            attachMiscBean(top, averageSources[0], list);
            int listSize = list.size();
            for (int i = 1; i < averageSources.length; ++i) {
                if (null == averageSources[i] && canAvgEmpty) {
                    continue;
                }
                List<Double> avgScores = cardType == CardType.scoreCardCode ?
                        scoreRuleInstanceDao.findAverageByOneCardCode(card, averageSources[i], maxScoreTreeLevel) :
                        scoreRuleInstanceDao.findAverageByOneCardId(card, averageSources[i], maxScoreTreeLevel);
                if (CollectionUtils.isEmpty(avgScores)) {
                    throw new IllegalArgumentException("averageSources: " + Arrays.toString(averageSources[i]) + ", no records");
                }
                if (avgScores.size() != listSize) {
                    throw new UnsupportedOperationException("avg score size is not equal to current record, maybe definition changed, "
                            + "current" + cardType + ": " + card + ", averageSources: "
                            + Arrays.toString(averageSources[i]));
                }
                for (int j = 1; j < listSize; ++j) {
                    ((MiscItemBean) list.get(j).getExtraProp()).avgScores[i - 1] = avgScores.get(j);
                }
            }
        }
        applyFEState(list, getStrategy());
        return top;
    }

    // 按照目前的需求，在实现该方法的时候，不需要对原始数据进行更新，因为只有主标签才需要这方法，不存在需要更新值，更新分的问题
    @Override
    public TopTagRuleInstance openForWriteAlsoAverage(
            Long currentScoreBizSrcId, Object[][] averageSources, int maxScoreTreeLevel, boolean canAvgEmpty) {
        try {
            if (null == currentScoreBizSrcId) {
                throw new IllegalArgumentException("currentScoreBizSrcId cannot be empty");
            }
            if (null == averageSources[0] || averageSources[0].length == 0) {
                throw new IllegalArgumentException("averageSources[0] is columnNames, and cannot be empty");
            }
            if (averageSources[0].length + 1 != averageSources.length) {
                throw new IllegalArgumentException("averageSources.length must be equal to 1 + averageSources[0].length");
            }

            ScoreBizSource scoreBizSource = findScoreBizSourceOrThrowException(currentScoreBizSrcId);
            List<TagRuleInstance> list = scoreRuleInstanceDao.findByBizSrcIdAndLevel(scoreBizSource.getId(), maxScoreTreeLevel);
            if (CollectionUtils.isEmpty(list)) {
                throw new IllegalArgumentException("scoreBizSrcId: " + currentScoreBizSrcId + ", maxScoreTreeLevel: "
                        + maxScoreTreeLevel + ", no records found");
            }
            TopTagRuleInstance top = prepareTopTagRuleInstance(scoreBizSource, list, false);
            if (!(averageSources[0] instanceof String[])) {
                throw new IllegalArgumentException("averageSources[0] should be of type String[]");
            }
            attachMiscBean(top, (String[]) averageSources[0], list);
            int listSize = list.size();
            for (int i = 1; i < averageSources.length; ++i) {
                if (null == averageSources[i] && canAvgEmpty) {
                    continue;
                }
                if (!(averageSources[i] instanceof Long[])) {
                    throw new IllegalArgumentException("averageSources[i>=0] should be type of Long[]");
                }
                List<Double> avgScores = scoreRuleInstanceDao.findAverage((Long[]) averageSources[i], maxScoreTreeLevel);
                if (CollectionUtils.isEmpty(avgScores)) {
                    throw new IllegalArgumentException("averageSources: " + Arrays.toString(averageSources[i]) + ", no records");
                }
                if (avgScores.size() != listSize) {
                    throw new UnsupportedOperationException("avg score size is not equal to current record, maybe definition changed, "
                            + "currentScoreBizSrcId: " + currentScoreBizSrcId + ", averageSources: " + Arrays.toString(averageSources[i]));
                }
                for (int j = 1; j < listSize; ++j) {
                    ((MiscItemBean) list.get(j).getExtraProp()).avgScores[i - 1] = avgScores.get(j);
                }
            }
            applyFEState(list, getStrategy());
            return top;
        } finally {
            removeStrategy();
        }
    }

    private void attachMiscBean(TopTagRuleInstance top, String[] columnNames, List<TagRuleInstance> list) {
        MiscMetaBean miscMetaBean = new MiscMetaBean();
        miscMetaBean.avgScores = columnNames;
        top.setExtraProp(miscMetaBean);

        for (int i = 1; i < list.size(); ++i) {
            TagRuleInstance ruleInstance = list.get(i);
            MiscItemBean miscItemBean = new MiscItemBean();
            miscItemBean.avgScores = new Double[columnNames.length];
            ruleInstance.setExtraProp(miscItemBean);
        }
    }

    /**
     * 按照目前的需求，在实现该方法的时候，如果数据已经存在，不需要对原始数据进行更新，因为只有主标签才需要这方法，不存在需要更新值，更新分的问题
     * 如果数据不存在，给空值空分即可
     */
    @Override
    @Transactional
    public TopTagRuleInstance openForWriteAlsoAverage(
            String bizId, String scoreCardCode, String context,
            String[][] averageSources, int maxScoreTreeLevel, boolean canAvgEmpty) {
        try {
            return openForWriteAlsoAverageByCard(
                    bizId, scoreCardCode, context, averageSources, maxScoreTreeLevel, CardType.scoreCardCode, canAvgEmpty);
        } finally {
            removeStrategy();
        }
    }

    enum CardType {
        scoreCardCode, scoreCardId
    }

    private TopTagRuleInstance openForWriteAlsoAverageByCard(
            String bizId, String card, String context, String[][] averageSources,
            int maxScoreTreeLevel, CardType type, boolean canAvgEmpty) {
        if (StringUtils.isEmpty(card) || StringUtils.isEmpty(context)) {
            throw new IllegalArgumentException(type + ", context cannot be empty");
        }
        if (null == averageSources[0] || averageSources[0].length == 0) {
            throw new IllegalArgumentException("averageSources[0] is columnNames, and cannot be empty");
        }
        if (averageSources[0].length * 2 + 1 != averageSources.length) {
            throw new IllegalArgumentException("averageSources.length must be equal to 1 + 2 * averageSources[0].length");
        }

        ScoreBizSource scoreBizSource = type == CardType.scoreCardCode ?
                findScoreBizSource(card, context) : findScoreBizSourceByCardId(card, context);
        List<TagRuleInstance> list;
        boolean isNew = false;
        if (null == scoreBizSource) {
            ScoreCardModel scoreCardModel = findScoreCardOrThrowException(card, type);
            scoreBizSource = new ScoreBizSource(StringUtils.isEmpty(bizId) ? null : bizId, context, scoreCardModel);
            scoreBizSource.setScoreCardModel(scoreCardModel);
            Long startId = scoreRuleInstanceDao.createNewSet();
            scoreBizSource.setId(startId);
            updateAuditInfo(scoreBizSource, true);
            scoreRuleInstanceDao.insertBizSrc(scoreBizSource);
            list = initInstanceList(scoreBizSource, maxScoreTreeLevel, true).get_2();
            isNew = true;
        } else {
            list = scoreRuleInstanceDao.findByBizSrcIdAndLevel(scoreBizSource.getId(), maxScoreTreeLevel);
            if (CollectionUtils.isEmpty(list)) {
                throw new IllegalArgumentException(type + ": " + card + ", context: "
                        + context + ", maxScoreTreeLevel: " + maxScoreTreeLevel + ", no records found");
            }
        }
        TopTagRuleInstance top = prepareTopTagRuleInstance(scoreBizSource, list, false);
        if (isNew) {
            scoreRuleInstanceDao.insertList(list);
        }
        top.setMaxDepth(maxScoreTreeLevel); // maybe redundant
        attachMiscBean(top, averageSources[0], list);

        int listSize = list.size();
        for (int i = 1; i < averageSources.length; i += 2) {
            if (null == averageSources[i] && canAvgEmpty) {
                continue;
            }
            int arrLen = averageSources[i].length;
            Tuple2<String, String>[] tuple2s = new Tuple2[arrLen];
            for (int j = 0; j < arrLen; ++j) {
                tuple2s[j] = new Tuple2<>(averageSources[i + 1][j], averageSources[i][j]);
            }
            List<Double> avgScores = type == CardType.scoreCardCode ?
                    scoreRuleInstanceDao.findAverageByCardCode(tuple2s, maxScoreTreeLevel) :
                    scoreRuleInstanceDao.findAverageByCardId(tuple2s, maxScoreTreeLevel);
            if (CollectionUtils.isEmpty(avgScores)) {
                throw new IllegalArgumentException("averageSources: " + Arrays.toString(averageSources[i]) + ", " +
                        Arrays.toString(averageSources[i + 1]) + ", no records");
            }
            if (avgScores.size() != listSize) {
                throw new UnsupportedOperationException("avg score size is not equal to current record, maybe definition changed, "
                        + "current" + type + ": " + card + ", context: " + context + ", averageSources: "
                        + Arrays.toString(averageSources[i]) + ", " + Arrays.toString(averageSources[i + 1]));
            }
            for (int j = 1; j < listSize; ++j) {
                ((MiscItemBean) list.get(j).getExtraProp()).avgScores[(i - 1) / 2] = avgScores.get(j);
            }
        }
        applyFEState(list, getStrategy());
        return top;
    }

    private void updateAuditInfo(ScoreBizSource scoreBizSource, boolean insert) {
        String currentUser = getCurrentUser();
        try {
            if (StringUtils.isEmpty(currentUser)) {
                currentUser = CURRENT_USER.get();
            }
        } catch (Exception e) {
            currentUser = null;
        }
        if (insert) {
            scoreBizSource.setCreatedBy(currentUser);
        } else {
            scoreBizSource.setUpdatedBy(currentUser);
        }
    }

    private String getCurrentUser() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            return RequestUtil.getLoginUserUmNo(request);
        }
        return null;
    }

    /**
     * 按照目前的需求，在实现该方法的时候，如果数据已经存在，不需要对原始数据进行更新，因为只有主标签才需要这方法，不存在需要更新值，更新分的问题
     * 如果数据不存在，给空值空分即可
     */
    @Override
    @Transactional
    public TopTagRuleInstance openForWriteAlsoAverageByCardId(
            String bizId, String scoreCardId, String context, String[][] averageSources, int maxScoreTreeLevel, boolean canAvgEmpty) {
        try {
            return openForWriteAlsoAverageByCard(
                    bizId, scoreCardId, context, averageSources, maxScoreTreeLevel, CardType.scoreCardId, canAvgEmpty);
        } finally {
            removeStrategy();
        }
    }

    @Override
    @Transactional
    public TopTagRuleInstance copyIfNotExistOrOpenAlsoAverage(
            ScoreBizSource copyFrom, String context, String[][] averageSources, int maxScoreTreeLevel, boolean canAvgEmpty) {
        try {
            return copyIfNotExistOrOpenAlsoAverageByCard(
                    copyFrom, context, averageSources, maxScoreTreeLevel, CardType.scoreCardCode, canAvgEmpty);
        } finally {
            removeStrategy();
        }
    }

    private TopTagRuleInstance copyIfNotExistOrOpenAlsoAverageByCard(
            ScoreBizSource copyFrom, String context, String[][] averageSources,
            int maxScoreTreeLevel, CardType type, boolean canAvgEmpty) {
        if (ObjectUtils.isEmpty(copyFrom) || StringUtils.isEmpty(context)) {
            throw new IllegalArgumentException(type + ", context cannot be empty");
        }
        if (ObjectUtils.isEmpty(averageSources[0]) || averageSources[0].length == 0) {
            throw new IllegalArgumentException("averageSources[0] is columnNames, and cannot be empty");
        }
        if (averageSources[0].length * 2 + 1 != averageSources.length) {
            throw new IllegalArgumentException("averageSources.length must be equal to 1 + 2 * averageSources[0].length");
        }
        ScoreCardModel scoreCardModel = copyFrom.getScoreCardModel();
        String card = type == CardType.scoreCardCode ? scoreCardModel.getScoreCardCode() : scoreCardModel.getId();
        ScoreBizSource scoreBizSource = type == CardType.scoreCardCode ?
                findScoreBizSource(card, context) : findScoreBizSourceByCardId(card, context);
        List<TagRuleInstance> list;
        if (ObjectUtils.isEmpty(scoreBizSource)) {
            TopTagRuleInstance copyInstance = createCommitteeCopy(copyFrom, context, PRIMARY_TAG);
            list = flat(copyInstance);
            list = list.stream().filter(instance -> instance.level() <= maxScoreTreeLevel).collect(Collectors.toList());
            list.forEach(instance -> instance.setChildren(null));
            scoreBizSource = scoreRuleInstanceDao.findBizSrcById(list.get(0).getId());
        } else {
            list = scoreRuleInstanceDao.findByBizSrcIdAndLevel(scoreBizSource.getId(), maxScoreTreeLevel);
            if (CollectionUtils.isEmpty(list)) {
                throw new IllegalArgumentException(type + ": " + card + ", context: "
                        + context + ", maxScoreTreeLevel: " + maxScoreTreeLevel + ", no records found");
            }
        }
        TopTagRuleInstance top = prepareTopTagRuleInstance(scoreBizSource, list, false);
        top.setMaxDepth(maxScoreTreeLevel); // maybe redundant
        attachMiscBean(top, averageSources[0], list);

        int listSize = list.size();
        for (int i = 1; i < averageSources.length; i += 2) {
            if (null == averageSources[i] && canAvgEmpty) {
                continue;
            }
            int arrLen = averageSources[i].length;
            Tuple2<String, String>[] tuple2s = new Tuple2[arrLen];
            for (int j = 0; j < arrLen; ++j) {
                tuple2s[j] = new Tuple2<>(averageSources[i + 1][j], averageSources[i][j]);
            }
            List<Double> avgScores = type == CardType.scoreCardCode ?
                    scoreRuleInstanceDao.findAverageByCardCode(tuple2s, maxScoreTreeLevel) :
                    scoreRuleInstanceDao.findAverageByCardId(tuple2s, maxScoreTreeLevel);
            if (CollectionUtils.isEmpty(avgScores)) {
                throw new IllegalArgumentException("averageSources: " + Arrays.toString(averageSources[i]) + ", " +
                        Arrays.toString(averageSources[i + 1]) + ", no records");
            }
            if (avgScores.size() != listSize) {
                throw new UnsupportedOperationException("avg score size is not equal to current record, maybe definition changed, "
                        + "current" + type + ": " + card + ", context: " + context + ", averageSources: "
                        + Arrays.toString(averageSources[i]) + ", " + Arrays.toString(averageSources[i + 1]));
            }
            for (int j = 1; j < listSize; ++j) {
                ((MiscItemBean) list.get(j).getExtraProp()).avgScores[(i - 1) / 2] = avgScores.get(j);
            }
        }
        applyFEState(list, getStrategy());
        return top;
    }

    private Tuple2<List<TagRuleDef>, List<TagRuleInstance>> initInstanceList(
            ScoreBizSource scoreBizSource, int maxScoreTreeLevel, boolean needBuildDefTree) {
        List<TagRuleDef> ruleDefs = scoreDefService.findByScoreCardCodeAndLevel(
                scoreBizSource.getScoreCardModel().getScoreCardCode(), maxScoreTreeLevel, needBuildDefTree);
        if (CollectionUtils.isEmpty(ruleDefs)) {
            throw new IllegalArgumentException("scoreBizSrcId: " + scoreBizSource.getId() + ", scoreCardCode: " +
                    scoreBizSource.getScoreCardModel().getScoreCardCode() + ", maxScoreTreeLevel: " +
                    maxScoreTreeLevel + ", no records found");
        }
        Long startId = scoreBizSource.getId();
        List<TagRuleInstance> list = instantiationFromDefs(ruleDefs, startId);
        return new Tuple2<>(ruleDefs, list);
    }

    private List<TagRuleInstance> instantiationFromDefs(List<TagRuleDef> ruleDefs, Long startId) {
        List<TagRuleInstance> list = new ArrayList<>(ruleDefs.size());
        Queue<Long> queue = new ArrayDeque<>();
        TagRuleDef lastP = null;
        Long pId = null;
        for (TagRuleDef tagRuleDef : ruleDefs) {
            if ((tagRuleDef.parent() != lastP || null == lastP)) {
                pId = queue.poll();
                lastP = tagRuleDef.parent();
            }
            TagRuleInstance instance = new TagRuleInstance();
            instance.setDefinition(tagRuleDef);
            instance.setLevel(tagRuleDef.getLevel());
            instance.setScoreRuleDefId(tagRuleDef.getId());
            instance.setId(startId++);
            if (null != tagRuleDef.parent()) {
                instance.setParentInstanceId(pId);
            }
            list.add(instance);

            instance.setParentInstanceId(pId);
            if (tagRuleDef.hasChild()) {
                queue.add(instance.getId());
            }
        }
        return list;
    }

    private TagRuleInstance instantiationFromDef(TagRuleDef ruleDef) {
        TagRuleInstance instance = new TagRuleInstance();
        instance.setDefinition(ruleDef);
        instance.setLevel(ruleDef.getLevel());
        instance.setScoreRuleDefId(ruleDef.getId());
        return instance;
    }

    @Override
    @Transactional
    public TopTagRuleInstance openCompleteForWrite(Long currentScoreBizSrcId) {
        try {
            if (null == currentScoreBizSrcId) {
                throw new IllegalArgumentException("currentScoreBizSrcId cannot be empty");
            }
            ScoreBizSource scoreBizSource = findScoreBizSourceOrThrowException(currentScoreBizSrcId);
            return openAlreadyExistedCompleteForWrite(scoreBizSource);
        } finally {
            removeStrategy();
        }
    }

    @Override
    @Transactional
    public TopTagRuleInstance createACopyIfNotExistOrOpen(Long existedScoreBizSrcId, String currentContext) {
        return createACopyIfNotExistOrOpen(existedScoreBizSrcId, currentContext, true);
    }

    @Override
    @Transactional
    public TopTagRuleInstance createACopyIfNotExistOrOpen(String bizId, Long existedScoreBizSrcId, String currentContext) {
        try {
            ScoreBizSource scoreBizSource = findScoreBizSourceOrThrowException(existedScoreBizSrcId);
            ScoreBizSource mayCurrent = findScoreBizSourceByCardId(scoreBizSource.getScoreCardModel().getId(), currentContext);
            if (null != mayCurrent) {
                return openAlreadyExistedCompleteForWrite(mayCurrent);
            }
            if (null != bizId) {
                scoreBizSource.setBizId(bizId);
            }
            return createACopy(scoreBizSource, currentContext, true);
        } finally {
            removeStrategy();
        }
    }

    @Override
    @Transactional
    public TopTagRuleInstance createACopyIfNotExistOrOpen(String scoreCardCode, String existedContext, String currentContext) {
        return createACopyIfNotExistOrOpen(scoreCardCode, existedContext, currentContext, true);
    }

    @Override
    @Transactional
    public TopTagRuleInstance createACopyIfNotExistOrOpenByCardId(String scoreCardId, String existedContext, String currentContext) {
        return createACopyIfNotExistOrOpenByCardId(scoreCardId, existedContext, currentContext, true);
    }

    @Override
    @Transactional
    public TopTagRuleInstance createACopyIfNotExistOrOpen(Long existedScoreBizSrcId, String currentContext, boolean isLastSaveStateCopiedIfNotExist) {
        try {
            ScoreBizSource scoreBizSource = findScoreBizSourceOrThrowException(existedScoreBizSrcId);
            ScoreBizSource mayCurrent = findScoreBizSource(scoreBizSource.getScoreCardModel().getScoreCardCode(), currentContext);
            if (null != mayCurrent) {
                return openAlreadyExistedCompleteForWrite(mayCurrent);
            }
            return createACopy(scoreBizSource, currentContext, isLastSaveStateCopiedIfNotExist);
        } finally {
            removeStrategy();
        }
    }

    @Override
    @Transactional
    public TopTagRuleInstance createACopyIfNotExistOrOpen(String scoreCardCode, String existedContext, String currentContext, boolean isLastSaveStateCopiedIfNotExist) {
        try {
            ScoreBizSource mayCurrent = findScoreBizSource(scoreCardCode, currentContext);
            if (null != mayCurrent) {
                return openAlreadyExistedCompleteForWrite(mayCurrent);
            }
            return createACopy(findScoreBizSourceOrThrowException(scoreCardCode, existedContext), currentContext, isLastSaveStateCopiedIfNotExist);
        } finally {
            removeStrategy();
        }
    }

    @Override
    @Transactional
    public TopTagRuleInstance createACopyIfNotExistOrOpenByCardId(String scoreCardId, String existedContext, String currentContext, boolean isLastSaveStateCopiedIfNotExist) {
        try {
            ScoreBizSource mayCurrent = findScoreBizSourceByCardId(scoreCardId, currentContext);
            if (null != mayCurrent) {
                return openAlreadyExistedCompleteForWrite(mayCurrent);
            }
            return createACopy(findScoreBizSourceByCardIdOrThrowException(scoreCardId, existedContext), currentContext, isLastSaveStateCopiedIfNotExist);
        } finally {
            removeStrategy();
        }
    }

    @Override
    public TopTagRuleInstance eval(Long scoreBizSrcId, TopTagRuleInstance tree) {
        try {
            if (null == scoreBizSrcId) {
                throw new IllegalArgumentException("scoreBizSrcId cannot be empty");
            }
            ScoreBizSource scoreBizSource = findScoreBizSourceOrThrowException(scoreBizSrcId);
            return evalScore(scoreBizSource, tree);
        } finally {
            removeStrategy();
        }
    }

    private TopTagRuleInstance evalScore(ScoreBizSource scoreBizSource, TopTagRuleInstance tree) {
        ValueScoreStrategy strategy = getStrategy();
        rebuildTreeFromFE(tree);
        List<TagRuleInstance> list = flat(tree);
        boolean forbidCreateRootModel = tree.getMaxDepth() != TERTIARY_TAG;
        RuleContext ruleContext = ruleContextFactory.create(scoreBizSource, scoreBizSource.getScoreCardModel(), forbidCreateRootModel);
        for (TagRuleInstance instance : list) {
            if (instance.level() == TREE_ROOT) {
                continue;
            }
            if (strategy.needValue(instance.getDefinition())) {
                if (StringUtils.isBlank(instance.getValueSource()) && instance.isSuit()) {
                    throw new InputInCompleteException(instance.getDefinition().getDisplayName() +
                            " value cannot be blank");
                }
                processValueInput(ruleContext, instance, strategy);
            }
        }
        list.forEach(instance -> instance.setChildren(null));
        TopTagRuleInstance top = prepareTopTagRuleInstance(scoreBizSource, list, false);
        conditionalTagWeightHandler.handleInstance(list, ruleContext);
        List<TagRuleInstance> updatedParents = new ArrayList<>();
        handleParentInstance(list, updatedParents::add, strategy);
        applyFEState(list, strategy);
        return top;
    }

    private TopTagRuleInstance createACopy(ScoreBizSource prototype, String currentContext, boolean isLastSaveStateCopied) {
        ScoreBizSource newCopy = prototype.createACopy(currentContext);
        if (isLastSaveStateCopied) {
            newCopy.setLastSaveSubmit(prototype.isLastSaveSubmit());
        }
        Long newSet = scoreRuleInstanceDao.createNewSet();
        newCopy.setId(newSet);
        List<TagRuleInstance> list = scoreRuleInstanceDao.findByBizSrcIdWithDef(prototype.getId());
        if (CollectionUtils.isEmpty(list)) {
            throw new IllegalArgumentException("scoreBizSrcId: " + prototype.getId() + ", no records found");
        }
        updateAuditInfo(newCopy, true);
        scoreRuleInstanceDao.insertBizSrc(newCopy);

        final ValueScoreStrategy strategy = getStrategy();
        final RuleContext ruleContext = ruleContextFactory.create(newCopy, newCopy.getScoreCardModel());
        final StatusBean statusBean = handlePossibleDefChanges(
                list, newCopy, ChangedRecords4CopyCase::new, strategy, ruleContext);
        list = statusBean.list;
        if (null != statusBean.changedRecords) {
            ((ChangedRecords4CopyCase) statusBean.changedRecords).list = list;
        }
        for (int i = 0; i < list.size(); ++i) {
            list.get(i).setId(newSet + i);
        }

        conditionalTagWeightHandler.handleInstance(list, ruleContext);
        return openForWriteFromExistingList(newCopy, ruleContext, statusBean, strategy);
    }

    private TopTagRuleInstance createCommitteeCopy(ScoreBizSource prototype, String currentContext, int maxScoreTreeLevel) {
        ScoreBizSource newCopy = prototype.createACopy(currentContext);
        // 930需求审核岗默认保存状态(保守起见:基于要拷贝的前手保存状态)
        newCopy.setLastSaveSubmit(prototype.isLastSaveSubmit());
        Long newSet = scoreRuleInstanceDao.createNewSet();
        newCopy.setId(newSet);
        List<TagRuleInstance> list = scoreRuleInstanceDao.findByBizSrcIdWithDef(prototype.getId());
        if (CollectionUtils.isEmpty(list)) {
            throw new IllegalArgumentException("scoreBizSrcId: " + prototype.getId() + ", no records found");
        }
        updateAuditInfo(newCopy, true);
        scoreRuleInstanceDao.insertBizSrc(newCopy);

        final ValueScoreStrategy strategy = getStrategy();
        final RuleContext ruleContext = ruleContextFactory.create(newCopy, newCopy.getScoreCardModel());
        final StatusBean statusBean = handlePossibleDefChanges(
                list, newCopy, ChangedRecords4CopyCase::new, strategy, ruleContext);
        list = statusBean.list;
        list = list.stream().filter(instance -> instance.level() <= maxScoreTreeLevel).collect(Collectors.toList());
        list.forEach(instance -> {
            instance.setManualScore(instance.score());
            instance.setAutoScoreValue(null);
        });
        if (null != statusBean.changedRecords) {
            ((ChangedRecords4CopyCase) statusBean.changedRecords).list = list;
        }
        for (int i = 0; i < list.size(); ++i) {
            list.get(i).setId(newSet + i);
        }

        conditionalTagWeightHandler.handleInstance(list, ruleContext);
        return openForWriteFromExistingList(newCopy, ruleContext, statusBean, strategy);
    }

    private IChangedRecords newChangedRecords() {
        return new ChangedRecords();
    }

    private TopTagRuleInstance openAlreadyExistedCompleteForWrite(ScoreBizSource scoreBizSource) {
        ScoreCardModel scoreCard = scoreBizSource.getScoreCardModel();
        RuleContext ruleContext = ruleContextFactory.create(scoreBizSource, scoreCard);

        List<TagRuleInstance> list = scoreRuleInstanceDao.findByBizSrcIdWithDef(scoreBizSource.getId());
        if (CollectionUtils.isEmpty(list)) {
            throw new IllegalArgumentException("scoreBizSrcId: " + scoreBizSource.getId() + ", no records found");
        }
        ValueScoreStrategy strategy = getStrategy();
        final StatusBean statusBean = handlePossibleDefChanges(
                list, scoreBizSource, this::newChangedRecords, strategy, ruleContext);
        conditionalTagWeightHandler.handleInstance(statusBean.list, ruleContext);
        TopTagRuleInstance top = openForWriteFromExistingList(scoreBizSource, ruleContext, statusBean, strategy);
        if (!statusBean.changedRecords.isEmpty()) {
            updateAuditInfo(scoreBizSource, false);
            scoreRuleInstanceDao.updateScoreBizSrcForSave(scoreBizSource);
        }
        return top;
    }

    private TopTagRuleInstance openForWriteFromExistingList(
            final ScoreBizSource scoreBizSource, final RuleContext ruleContext, final StatusBean statusBean,
            final ValueScoreStrategy strategy) {
        // 处理标签适用属性
        handleRuleInstanceSuitAttribute(scoreBizSource.getScoreCardModel().getScoreCardCode(), ruleContext, statusBean.top);

        if (ruleContext.hasModel()) {
            applyModel(ruleContext, statusBean.list, statusBean.changedRecords, strategy);
        }
        if (statusBean.needRecalcParent && !statusBean.changedRecords.isEmpty()) {
            handleParentInstance(statusBean.list, instance -> addUpdate(instance, statusBean.changedRecords), strategy);
        }

        statusBean.changedRecords.updateDb();

        applyFEState(statusBean.list, strategy);
        return statusBean.top;
    }

    private StatusBean handlePossibleDefChanges(
            List<TagRuleInstance> list, ScoreBizSource scoreBizSource,
            Supplier<IChangedRecords> changedRecordsSupplier, ValueScoreStrategy strategy, RuleContext ruleContext) {
        TopTagRuleInstance top = prepareTopTagRuleInstance(scoreBizSource, list, false);

        boolean hasConditionalTagFeature = scoreBizSource.getScoreCardModel().scoreFeatures().isEnable(ConditionalTagExist);
        int latestCount;

        IChangedRecords changedRecords = changedRecordsSupplier.get();
        boolean needRecalcParent = true;

        List<TagRuleDef> latestDefs = null;
        if (hasConditionalTagFeature) {
            latestDefs = scoreDefService.findScoreRulesByCode(scoreBizSource.getScoreCardModel().getScoreCardCode());
            if (CollectionUtils.isEmpty(latestDefs)) {
                throw new IllegalStateException("currentScoreBizSrcId: " + scoreBizSource.getId() + " has no definition, maybe been deleted");
            }
            conditionalTagHandler.handleDef4Exclude(latestDefs, ruleContext);
            latestCount = latestDefs.size();
        } else {
            latestCount = scoreDefService.countByScoreCardCode(scoreBizSource.getScoreCardModel().getScoreCardCode());
        }
        if (latestCount == 0) {
            throw new IllegalStateException("currentScoreBizSrcId: " + scoreBizSource.getId() + " has no definition, maybe been deleted");
        }
        if (hasConditionalTagFeature || latestCount != list.size() || list.stream().anyMatch(ruleInstance -> ruleInstance.getDefinition().isHistory())) {
            // some definitions have changed or (always consider conditional tag feature if enabled)
            List<TagRuleInstance> allChanged = list.stream()
                    .filter(ruleInstance -> ruleInstance.getDefinition().isHistory())
                    .collect(Collectors.toList());
            long lastMaxId = list.get(list.size() - 1).getId();
            if (!hasConditionalTagFeature && list.size() - latestCount == allChanged.size()) {
                // only need remove history record. It's very rare (Strictly limited) to meet the condition
                // 如果只是删除规则，说明删除的不影响现存的规则，也就是不需要满足权重和等于1这个约束，自然也就是不涉及加权计算分数
                // 因此只需要删除即可，这里不去验证是否定义错误
                removeHistory(list, allChanged, changedRecords);
                needRecalcParent = false;
            } else {
                // 可能新增了，可能删除了，可能更新版本了，可能 conditional tag features is enabled, 必须将全部原始定义查出
                // todo ScoreTagConfig版本控制; ScoreBizSrc 与 ScoreCardModel 主键关联以期版本控制; ScoreBizSrc 针对 ScoreTagConfig 状态保存()，如此便能在实际情况下减少 Def 的查询与处理
                buildDefOfInstance(list);
                if (null == latestDefs) { // hasConditionalTagFeature is false
                    latestDefs = scoreDefService.findScoreRulesByCode(scoreBizSource.getScoreCardModel().getScoreCardCode());
                }
                handleDefChanges(list.get(0), latestDefs.get(0), changedRecords, strategy, hasConditionalTagFeature);
                list = flat(list.get(0));
            }
            reorderId(list, changedRecords, lastMaxId);
        }
        return new StatusBean(list, changedRecords, top, needRecalcParent);
    }

    static class StatusBean {
        final List<TagRuleInstance> list;
        final IChangedRecords changedRecords;
        final TopTagRuleInstance top;
        final boolean needRecalcParent;

        StatusBean(List<TagRuleInstance> list, IChangedRecords changedRecords, TopTagRuleInstance top,
                   boolean needRecalcParent) {
            this.list = list;
            this.changedRecords = changedRecords;
            this.top = top;
            this.needRecalcParent = needRecalcParent;
        }
    }

    private void buildDefOfInstance(List<TagRuleInstance> instances) {
        instances.forEach(instance -> {
            if (null != instance.parent()) {
                instance.getDefinition().parent(instance.parent().getDefinition());
            }
        });
    }

    private void reorderId(List<TagRuleInstance> list, IChangedRecords records, Long lastMaxId) {
        Long startId = list.get(0).getId();
        long currentMaxId = startId + list.size() - 1;
        for (int i = 1; i < list.size(); ++i) {
            TagRuleInstance instance = list.get(i);
            Long id = startId + i;
            if (null == instance.getId()) {
                instance.setId(id);
            } else {
                if (!instance.getId().equals(id)) {
                    instance.setId(id);
                    if (id < lastMaxId) {
                        records.update(instance);
                    }
                }
            }
        }
        records.afterRebuilt(currentMaxId, lastMaxId, list);
    }

    private void applyModel(RuleContext ruleContext, List<TagRuleInstance> list, IChangedRecords records, ValueScoreStrategy strategy) {
        for (int i = list.size() - 1; ; --i) {
            TagRuleInstance instance = list.get(i);
            if (instance.level() < TERTIARY_TAG) {
                return;
            }
            boolean update = false;
            if (strategy.needValue(instance.getDefinition()) &&
                    strategy.isAutoValue(instance.getDefinition())) {
                Object value = instance.getDefinition().getValueSourceExp().value(ruleContext, instance, instance.getValueSource());
                if (ValueSourceExp.isExceptionFallbackValue(value)) {
                    if (!StringUtils.isEmpty(instance.getValueSource())) {
                        update = true;
                    }
                    instance.setValueSource(null);
                } else {
                    String valueStr = toStr(value);
                    if ((StringUtils.isEmpty(valueStr) && !StringUtils.isEmpty(instance.getValueSource()))
                            || (null != valueStr && !valueStr.equals(instance.getValueSource()))) {
                        update = true;
                    }
                    instance.setValueSource(valueStr);
                }
            }
            if (strategy.needAutoScore(instance.getDefinition()) && instance.isSuit()) {
                if (StringUtils.isBlank(instance.getValueSource()) && instance.getDefinition().getDefaultScore() != null) {
                    instance.setAutoScoreValue(instance.getDefinition().getDefaultScore());
                } else {
                    Double score = scoreRuleFactory.rule(instance).calc(ruleContext);
                    if ((null == score && null != instance.getAutoScoreValue()) || (null != score
                            && null == instance.getAutoScoreValue()) || (null != score && null != instance.getAutoScoreValue()
                            && (score - instance.getAutoScoreValue()) >= SCORE_PRECISION)) {
                        update = true;
                    }
                    instance.setAutoScoreValue(score);
                }
            }
            if (update) {
                records.update(instance);
            }
        }
    }

    private void handleParentInstance(
            List<TagRuleInstance> list, Consumer<TagRuleInstance> callback, ValueScoreStrategy strategy) {
        int startIndex = list.size() - 1;

        for (int i = startIndex; i >= 0; --i) {
            TagRuleInstance instance = list.get(i);
            if (instance.level() > SECONDARY_TAG || instance.level() < PRIMARY_TAG) {
                continue;
            }
            if (strategy.needAutoScore(instance.getDefinition())) {
                if (instance.getDefinition().getScoreType() == SumByWeight && instance.hasChild()) {
                    Double score = SumByWeightScoreRule.calculate(instance);
                    if (null == score && null == instance.getAutoScoreValue()) {
                        continue;
                    }
//                    if (null == score || null == instance.getAutoScoreValue()) {
                    instance.setAutoScoreValue(score);
                    if (null != callback) {
                        callback.accept(instance);
                    }
//                    } else {
//                        if (Math.abs(score - instance.getAutoScoreValue()) >= SCORE_PRECISION) {
//                            instance.setAutoScoreValue(score);
//                            if (null != callback) {
//                                callback.accept(instance);
//                            }
//                        }
//                    }
                }
            }
        }
    }

    private void addUpdate(TagRuleInstance ruleInstance, IChangedRecords records) {
        if (null != ruleInstance.getId()) {
            records.update(ruleInstance);
        }
    }

    // 对于修改 parent ruleParentId 不会做保留历史的处理(如果要处理这种情况，需要更多的搜索与判断)
    // 隐含条件，ruleCode 不可变
    // 根节点不变
    private void handleDefChanges(TagRuleInstance instance, TagRuleDef def, IChangedRecords records,
                                  ValueScoreStrategy strategy, boolean hasConditionalTagFeature) {
        Queue<TagRuleInstance> instanceQueue = new ArrayDeque<>();
        Queue<TagRuleDef> defQueue = new ArrayDeque<>();
        Map<String, TagRuleInstance> parentMap = new HashMap<>();
        parentMap.put(def.getRuleCode(), instance);
        for (; null != instance || null != def; ) {
            if (null == def) {
                do {
                    deleteSelfAndDescendants(instance, records);
                } while (null != (instance = instanceQueue.poll()));
                break;
            }
            if (null == instance) {
                // 新创建instance，找到并且关联上parent
                do {
                    TagRuleInstance newInstance = instantiationFromDef(def);
                    linkParentAndAddInsert(def, newInstance, parentMap, records);
                } while (null != (def = defQueue.poll())); // todo
                break;
            }

            if (instance.level() < def.level()) {
                deleteSelfAndDescendants(instance, records);
                instance = instanceQueue.poll();
            } else if (instance.level() > def.level()) {
                // 新创建instance，找到并且关联上parent
                def = createNewInstanceAndForwardCursor(def, parentMap, records, defQueue);
            } else {
                int parentCompare;
                if (null != instance.getDefinition().parent() && null != def.parent()
                        && (parentCompare = instance.getDefinition().parent().getRuleCode().compareTo(def.parent().getRuleCode())) != 0) {
                    if (parentCompare < 0) {
                        deleteSelfAndDescendants(instance, records);
                        instance = instanceQueue.poll();
                    } else {
                        // 新创建instance，找到并且关联上parent
                        def = createNewInstanceAndForwardCursor(def, parentMap, records, defQueue);
                    }
                    continue;
                }

                if (!def.getRuleCode().equals(instance.getDefinition().getRuleCode())) {
                    if (Arrays.binarySearch(def.siblingCodes(), instance.getDefinition().getRuleCode()) >= 0) {
                        // 隐含条件，ruleCode 不变
                        // 新创建instance，找到并且关联上parent
                        def = createNewInstanceAndForwardCursor(def, parentMap, records, defQueue);
                    } else {
                        // instance已删除
                        if (!instance.getDefinition().isHistory() && !hasConditionalTagFeature) {
                            throw new IllegalStateException();
                        } // 只是验证一下
                        deleteSelfAndDescendants(instance, records);
                        instance = instanceQueue.poll();
                    }
                } else {
                    putIfCouldHaveChild(instance, parentMap);
                    if (def.isNoChange(instance.getDefinition())) {
                        if (instance.getDefinition().isHistory()) {
                            throw new IllegalStateException();
                        } // 只是验证一下
                        instance = offerIfHasChildAndForwardCursor(instance, instanceQueue);
                        def = offerIfHasChildAndForwardCursor(def, defQueue);
                    } else {
                        if (!instance.getDefinition().isHistory()) {
                            throw new IllegalStateException();
                        } // 只是验证一下
                        if (strategy.needValue(def) &&
                                !Objects.equals(instance.getDefinition().getValueSourceExp(), def.getValueSourceExp())) {
                            // 虽然值与分可以独立更新的，但是基于这种考虑: 分依赖于值，在值变化的时候，分应当失效
                            // 值的规则变更时候
                            records.update(instance);
                            instance.setValueSource(null);
                            instance.setManualScore(null);
                            instance.setAutoScoreValue(null);
                        } else if (!instance.getDefinition().isScoreRuleNoChange(def)) {
                            records.update(instance);
                            instance.setManualScore(null);
                            instance.setAutoScoreValue(null);
                        } else {

                        }
                        instance.setDefinition(def); // forgotten before
                        instance = offerIfHasChildAndForwardCursor(instance, instanceQueue);
                        def = offerIfHasChildAndForwardCursor(def, defQueue);
                    }
                }
            }
        }
        // 重新对parent计算分值（如需要的话），为了简单，不能维护状态，全量重新计算，后面可以优化
    }

    private TagRuleDef createNewInstanceAndForwardCursor(TagRuleDef def, Map<String, TagRuleInstance> parentMap,
                                                         IChangedRecords records, Queue<TagRuleDef> defQueue) {
        TagRuleInstance newInstance = instantiationFromDef(def);
        linkParentAndAddInsert(def, newInstance, parentMap, records);
        return offerIfHasChildAndForwardCursor(def, defQueue);
    }

    private void linkParentAndAddInsert(TagRuleDef def, TagRuleInstance instance,
                                        Map<String, TagRuleInstance> parentMap, IChangedRecords records) {
        TagRuleInstance parent = parentMap.get(def.parent().getRuleCode());
        if (null == parent) {
            throw new IllegalStateException(); // should not be here
        }
        instance.parent(parent);
        parent.getChildren().sort(Comparator.comparing(x -> x.getDefinition().getRuleCode()));
        putIfCouldHaveChild(instance, parentMap);
        records.newCreate(instance);
    }

    private void putIfCouldHaveChild(TagRuleInstance ruleInstance, Map<String, TagRuleInstance> map) {
        if (ruleInstance.level() < TERTIARY_TAG) {
            map.put(ruleInstance.getDefinition().getRuleCode(), ruleInstance);
        }
    }

    private List<TagRuleInstance> retrieveAllDescendants(TagRuleInstance ruleInstance) {
        if (!ruleInstance.hasChild()) {
            return Collections.emptyList();
        }
        List<TagRuleInstance> result = new ArrayList<>(ruleInstance.getChildren());
        if (ruleInstance.level() > SECONDARY_TAG) {
            return result;
        }
        Queue<TagRuleInstance> queue = new ArrayDeque<>(ruleInstance.getChildren());
        while (!queue.isEmpty()) {
            TagRuleInstance instance = queue.poll();
            if (instance.hasChild()) {
                result.addAll(instance.getChildren());
                if (instance.level() < SECONDARY_TAG) {
                    queue.addAll(instance.getChildren());
                }
            }
        }
        return result;
    }

    private <U extends HierarchyRecord<?>> U offerIfHasChildAndForwardCursor(U node, Queue<U> queue) {
        if (node.hasChild()) {
            queue.addAll((List<U>) node.getChildren());
        }
        return queue.poll();
    }

    private void removeHistory(List<TagRuleInstance> list, List<TagRuleInstance> history, IChangedRecords changed) {
        List<TagRuleInstance> topHistory = HierarchyRecord.findTop(history);
        topHistory.forEach(historyRecord -> {
            changed.delete(historyRecord);
            List<TagRuleInstance> descendants = retrieveAllDescendants(historyRecord);
            list.remove(historyRecord);
            if (!CollectionUtils.isEmpty(descendants)) {
                changed.deleteList(descendants);
                list.removeAll(descendants);
            }
            historyRecord.removeFromTree();
        });
    }

    @Override
    @Transactional
    public TopTagRuleInstance openCompleteForWrite(String bizId, String scoreCardCode, String context) {
        try {
            return openCompleteForWrite(bizId, scoreCardCode, context, CardType.scoreCardCode);
        } finally {
            removeStrategy();
        }
    }

    private TopTagRuleInstance openCompleteForWrite(String bizId, String card, String context, CardType cardType) {
        if (StringUtils.isEmpty(card) || StringUtils.isEmpty(context)) {
            throw new IllegalArgumentException(cardType + " or context cannot be empty");
        }
        ScoreBizSource scoreBizSource = cardType == CardType.scoreCardCode ?
                findScoreBizSource(card, context) : findScoreBizSourceByCardId(card, context);
        if (null != scoreBizSource) {
            if (null != bizId) {
                if (!bizId.equals(scoreBizSource.getBizId())) {
                    throw new IllegalStateException();
                }
            } else if (!StringUtils.isEmpty(scoreBizSource.getBizId())) {
                throw new IllegalStateException();
            } else {

            }
            return openAlreadyExistedCompleteForWrite(scoreBizSource);
        }
        ScoreCardModel scoreCardModel = findScoreCardOrThrowException(card, cardType);
        scoreBizSource = new ScoreBizSource(StringUtils.isBlank(bizId) ? null : bizId.trim(), context, scoreCardModel);
        return createCompleteNew(scoreBizSource);
    }

    @Override
    @Transactional
    public TopTagRuleInstance openCompleteForWriteByCardId(String bizId, String scoreCardId, String context) {
        try {
            return openCompleteForWrite(bizId, scoreCardId, context, CardType.scoreCardId);
        } finally {
            removeStrategy();
        }
    }

    abstract static class IChangedRecords {
        abstract void delete(TagRuleInstance ruleInstance);

        abstract void deleteList(List<TagRuleInstance> ruleInstances);

        abstract void update(TagRuleInstance ruleInstance);

        abstract void newCreate(TagRuleInstance ruleInstance);

        abstract void afterRebuilt(long currentMaxId, long lastMaxId, List<TagRuleInstance> list);

        abstract boolean isEmpty();

        abstract void updateDb();
    }

    class ChangedRecords4CopyCase extends IChangedRecords {
        List<TagRuleInstance> list;

        public ChangedRecords4CopyCase() {

        }


        @Override
        void delete(TagRuleInstance ruleInstance) {

        }

        @Override
        void deleteList(List<TagRuleInstance> ruleInstances) {

        }

        @Override
        void update(TagRuleInstance ruleInstance) {

        }

        @Override
        void newCreate(TagRuleInstance ruleInstance) {

        }

        @Override
        void afterRebuilt(long currentMaxId, long lastMaxId, List<TagRuleInstance> list) {

        }

        @Override
        boolean isEmpty() {
            return false;
        }

        @Override
        void updateDb() {
            scoreRuleInstanceDao.insertList(list);
        }
    }

    // 可以有两种实现，要么更新主键要么复制（更新）内容
    // 当前实现主要是更新内容，而不是只更新主键
    // 这里的实现有点怪异，有些代码没必要
    class ChangedRecords extends IChangedRecords {
        List<TagRuleInstance> newCreated;
        List<TagRuleInstance> deleted;
        Set<TagRuleInstance> updated;
        Long lastMaxId;
        Long currentMaxId;

        @Override
        void delete(TagRuleInstance ruleInstance) {
            if (null == deleted) {
                deleted = new ArrayList<>();
            }
            deleted.add(ruleInstance);
        }

        @Override
        void deleteList(List<TagRuleInstance> ruleInstances) {
            if (null == deleted) {
                deleted = new ArrayList<>();
            }
            deleted.addAll(ruleInstances);
        }

        @Override
        void update(TagRuleInstance ruleInstance) {
            if (null == updated) {
                updated = Collections.newSetFromMap(new IdentityHashMap<>());
            }
            updated.add(ruleInstance);
        }

        @Override
        void newCreate(TagRuleInstance ruleInstance) {
            if (null == newCreated) {
                newCreated = new ArrayList<>();
            }
            newCreated.add(ruleInstance);
        }

        @Override
        void afterRebuilt(long currentMaxId, long lastMaxId, List<TagRuleInstance> list) {
            setMaxId(currentMaxId, lastMaxId);
            moveCreate2UpdateAndMakeTailCreate(list);
            moveDelete2TailDelete();
        }

        void setMaxId(long currentMaxId, long lastMaxId) {
            this.currentMaxId = currentMaxId;
            this.lastMaxId = lastMaxId;
        }

        void moveCreate2UpdateAndMakeTailCreate(List<TagRuleInstance> list) {
            if (CollectionUtils.isEmpty(newCreated)) {
                return;
            }
            Iterator<TagRuleInstance> newCreatedIterator = newCreated.iterator();
            int newCreateCount = newCreated.size();
            while (newCreatedIterator.hasNext()) {
                TagRuleInstance instance = newCreatedIterator.next();
                if (instance.getId() <= lastMaxId) {
                    newCreatedIterator.remove();
                    update(instance);
                }
            }
            int size = list.size();
            for (int i = 1; i <= newCreateCount; ++i) {
                if (list.get(size - i).getId() > lastMaxId && !CollectionUtils.contains(newCreated.iterator(), list.get(size - i))) {
                    newCreate(list.get(size - i));
                }
            }
        }

        // tailDelete 的实现在updateDb里面
        void moveDelete2TailDelete() {
//            if (currentMaxId < lastMaxId) {
//                deleted.removeIf(ruleInstance -> ruleInstance.getId() <= currentMaxId);
//            }
        }

        @Override
        boolean isEmpty() {
            return CollectionUtils.isEmpty(newCreated) && CollectionUtils.isEmpty(deleted) &&
                    CollectionUtils.isEmpty(updated);
        }

        // 当前实现主要是更新内容，而不是只更新主键
        @Override
        void updateDb() {
//            if (!CollectionUtils.isEmpty(deleted)) {
//                scoreRuleInstanceDao.deleteInstances(deleted);
//            }
            if (null != lastMaxId && null != currentMaxId && lastMaxId > currentMaxId) {
                scoreRuleInstanceDao.deleteInstanceByRange(lastMaxId, currentMaxId);
            }
            if (!CollectionUtils.isEmpty(updated)) {
                //todo batch update
                for (TagRuleInstance instance : updated) {
                    scoreRuleInstanceDao.updateInstance(instance);
                }
            }
            if (!CollectionUtils.isEmpty(newCreated)) {
                scoreRuleInstanceDao.insertList(newCreated);
            }
        }
    }

    private void deleteSelfAndDescendants(TagRuleInstance ruleInstance, IChangedRecords records) {
        ruleInstance.removeFromTree();
        records.delete(ruleInstance);

        List<TagRuleInstance> descendants = retrieveAllDescendants(ruleInstance);
        if (!CollectionUtils.isEmpty(descendants)) {
            records.deleteList(descendants);
        }
    }

    private String valueArrayToStr(Object[] values) {
        return ValueSourceExp.valueArrayToStr(values);
    }

    private TopTagRuleInstance createCompleteNew(ScoreBizSource scoreBizSource) {
        ValueScoreStrategy strategy = getStrategy();
        Long startId = scoreRuleInstanceDao.createNewSet();
        scoreBizSource.setId(startId);
        updateAuditInfo(scoreBizSource, true);
        scoreRuleInstanceDao.insertBizSrc(scoreBizSource);
        ScoreCardModel scoreCard = scoreBizSource.getScoreCardModel();
        List<TagRuleDef> ruleDefs = scoreDefService.findScoreRulesByCode(scoreCard.getScoreCardCode());
        RuleContext ruleContext = ruleContextFactory.create(scoreBizSource, scoreCard);

        // handler
        if (scoreBizSource.getScoreCardModel().scoreFeatures().isEnable(ConditionalTagExist)) {
            conditionalTagHandler.handleDef4Exclude(ruleDefs, ruleContext);
            conditionalTagWeightHandler.handleDef(ruleDefs, ruleContext);
        }

        int maxDepth = ruleDefs.get(ruleDefs.size() - 1).level();

        List<TagRuleInstance> list = instantiationFromDefs(ruleDefs, startId);
        TopTagRuleInstance top = prepareTopTagRuleInstance(scoreBizSource, list, false);

        // 处理标签不适用属性
        handleRuleInstanceSuitAttribute(scoreCard.getScoreCardCode(), ruleContext, top);
        if (null == scoreBizSource.getBizId()) {
            //
        } else {
            Queue<TagRuleInstance> mayAffectedP = new ArrayDeque<>();
            TagRuleInstance lastP = null;
            for (TagRuleInstance ruleInstance : list) {
                if (ruleInstance.getLevel() != maxDepth) {
                    continue;
                }
                TagRuleDef ruleDef = ruleInstance.getDefinition();
                if (strategy.needValue(ruleDef) && strategy.isAutoValue(ruleDef)) {
                    Object value = ruleDef.getValueSourceExp().value(ruleContext, ruleInstance, ruleInstance.getValueSource());
                    if (ValueSourceExp.isExceptionFallbackValue(value)) {
                        continue;
                    }
                    ruleInstance.setValueSource(toStr(value));
                    if (strategy.needAutoScore(ruleInstance.getDefinition())) {
                        Double score = scoreRuleFactory.rule(ruleInstance).calc(ruleContext);
                        if (null != score) {
                            ruleInstance.setAutoScoreValue(score);
                            if (ruleInstance.parent() != lastP) {
                                mayAffectedP.offer(lastP = ruleInstance.parent());
                            }
                        }
                    }
                }
            }
            while (!mayAffectedP.isEmpty()) {
                TagRuleInstance p = mayAffectedP.poll();
                if (null == p) {
                    continue;
                }
                TagRuleDef ruleDef = p.getDefinition();
                if (strategy.isAutoValue(ruleDef) && strategy.isSumByWeight(ruleDef)) {
                    Double score = SumByWeightScoreRule.calculate(p);
                    if (null != score) {
                        p.setAutoScoreValue(score);
                        if (p.parent() != lastP) {
                            mayAffectedP.offer(lastP = p.parent());
                        }
                    }
                }
            }
        }
        scoreRuleInstanceDao.insertList(list);
        applyFEState(list, strategy);
        return top;
    }

    @Override
    @Transactional
    public TopTagRuleInstance fullSave(Long currentScoreBizSrcId, TopTagRuleInstance tree, boolean isTemp) {
        try {
            if (null == currentScoreBizSrcId) {
                throw new IllegalArgumentException("scoreBizSrcId cannot be empty");
            }
            ScoreBizSource scoreBizSource = findScoreBizSourceOrThrowException(currentScoreBizSrcId);
            return fullSave(scoreBizSource, tree, isTemp);
        } finally {
            removeStrategy();
        }
    }

    //todo 暂存和提交可能区别对待得不够彻底
    //todo 如果区分得够彻底，在提交的时候，有性能提升空间
    @Override
    @Transactional
    public TopTagRuleInstance fullSave(String scoreCardCode, String context, TopTagRuleInstance tree, boolean isTemp) {
        try {
            return fullSave(scoreCardCode, context, tree, CardType.scoreCardCode, isTemp);
        } finally {
            removeStrategy();
        }
    }

    @Override
    @Transactional
    public TopTagRuleInstance fullSaveByCardId(String scoreCardId, String context, TopTagRuleInstance tree, boolean isTemp) {
        try {
            return fullSave(scoreCardId, context, tree, CardType.scoreCardId, isTemp);
        } finally {
            removeStrategy();
        }
    }

    private TopTagRuleInstance fullSave(String card, String context, TopTagRuleInstance tree, CardType cardType, boolean isTemp) {
        if (StringUtils.isEmpty(card) || StringUtils.isEmpty(context)) {
            throw new IllegalArgumentException(cardType + " or context cannot be empty");
        }
        ScoreBizSource scoreBizSource = cardType == CardType.scoreCardCode ?
                findScoreBizSourceOrThrowException(card, context) :
                findScoreBizSourceByCardIdOrThrowException(card, context);
        return fullSave(scoreBizSource, tree, isTemp);
    }

    private TopTagRuleInstance fullSave(ScoreBizSource scoreBizSource, TopTagRuleInstance tree, boolean isTemp) {
        ValueScoreStrategy strategy = getStrategy();
        if (isTemp) {
            tempFullSave(scoreBizSource, tree, strategy);
            return null;
        }
        rebuildTreeFromFE(tree);
        List<TagRuleInstance> list = flat(tree);
        boolean forbidCreateRootModel = tree.getMaxDepth() != TERTIARY_TAG;
        RuleContext ruleContext = ruleContextFactory.create(scoreBizSource, scoreBizSource.getScoreCardModel(), forbidCreateRootModel);
        List<TagRuleInstance> toUpdate = list.stream().filter(instance -> {
            if (instance.level() == TREE_ROOT) {
                return false;
            }
            if (strategy.needValue(instance.getDefinition())) {
                // 需要值，但值为空且标签适用时，会抛异常，视具体业务要求而定
//                if (StringUtils.isBlank(instance.getValueSource()) && instance.isSuit()) {
//                    throw new InputInCompleteException(instance.getDefinition().getDisplayName() +
//                            " value cannot be blank");
//                }
                processValueInput(ruleContext, instance, strategy);
                return true;
            }
            if (!strategy.needAutoScore(instance.getDefinition()) || strategy.canAdjustScore(instance.getDefinition())) {
                if (null == instance.score() && instance.isSuit() && !mayBeScored(instance, strategy)) {
                    throw new InputInCompleteException(instance.getDefinition().getDisplayName() + " score must be input");
                }
                if (null != instance.getAutoScoreValue() && null != instance.getManualScore() &&
                        StringUtils.isBlank(instance.getManualScoreReason())) {
                    throw new InputInCompleteException(instance.getDefinition().getDisplayName() + " manual adjustment reason must be input");
                }
                if (StringUtils.isNotBlank(instance.getManualScoreReason()) && null == instance.getManualScore()) {
                    throw new InputInCompleteException(instance.getDefinition().getDisplayName() + " manual adjustment score must be input when reason is input");
                }
                return true;
            }

            if (strategy.needAutoScore(instance.getDefinition()) && !strategy.isSumByWeight(instance.getDefinition())) {
                // 目前的需求不应该走进来，因为自动打分栏不会因为取不到值或者打不了分而变为可输入，相反总是只读的
                if (null == instance.score()) {
                    throw new InputInCompleteException(instance.getDefinition().getDisplayName() + " score must be input");
                }
                if (null != instance.getManualScore()) {
                    return true;
                }
            }
//            if (instance.isSuit()) {
//                return false;
//            }
            return true;
        }).collect(Collectors.toList());
        updateScoreBizSrcSaveState(scoreBizSource, true);
        if (!toUpdate.isEmpty()) {
            toUpdate.forEach(scoreRuleInstanceDao::updateFromUI);
        }
        return refreshAfterSubmit(scoreBizSource, strategy);
    }

    private void processValueInput(RuleContext ruleContext, TagRuleInstance instance, ValueScoreStrategy strategy) {
        if (!strategy.needAutoScore(instance.getDefinition())) {
            return;
        }
        Double score;
        if (instance.isSuit()) {
            if (StringUtils.isBlank(instance.getValueSource()) && instance.getDefinition().getDefaultScore() != null) {
                score = instance.getDefinition().getDefaultScore();
            } else {
                score = scoreRuleFactory.rule(instance).calc(ruleContext);
            }
        } else {
            score = instance.getAutoScoreValue();
        }
        instance.setAutoScoreValue(score);
    }

    private boolean mayBeScored(TagRuleInstance instance, ValueScoreStrategy strategy) {
        return strategy.needAutoScore(instance.getDefinition()) && strategy.isSumByWeight(instance.getDefinition());
    }

    // 提交之后还允许改吗？readOnly
    private TopTagRuleInstance refreshAfterSubmit(ScoreBizSource scoreBizSource, ValueScoreStrategy strategy) {
        List<TagRuleInstance> list = scoreRuleInstanceDao.findByBizSrcIdWithDef(scoreBizSource.getId());
        TopTagRuleInstance top = prepareTopTagRuleInstance(scoreBizSource, list, false);
        List<TagRuleInstance> updatedParents = new ArrayList<>();
        RuleContext ruleContext = ruleContextFactory.create(scoreBizSource, scoreBizSource.getScoreCardModel(), true);
        conditionalTagWeightHandler.handleInstance(list, ruleContext);
        handleParentInstance(list, updatedParents::add, strategy);
        if (!updatedParents.isEmpty()) {
            updatedParents.forEach(scoreRuleInstanceDao::updateParentCauseByUI);
        }
        applyFEState(list, strategy);
        return top;
    }

    private boolean needSave(TagRuleInstance instance, ValueScoreStrategy strategy) {
        TagRuleDef tagRuleDef = instance.getDefinition();
        return strategy.needValue(tagRuleDef) ||
                !strategy.needAutoScore(tagRuleDef) ||
                strategy.canAdjustScore(tagRuleDef);
    }

    private void tempFullSave(ScoreBizSource scoreBizSource, TopTagRuleInstance tree, ValueScoreStrategy strategy) {
        List<TagRuleInstance> toUpdate = flat(tree);
//        List<TagRuleInstance> toUpdate = instances.stream().filter(instance -> needSave(instance, strategy))
//                .collect(Collectors.toList());
        if (toUpdate.isEmpty()) {
            return;
        }
        updateScoreBizSrcSaveState(scoreBizSource, false);
        toUpdate.forEach(scoreRuleInstanceDao::updateFromUI);
//        return null;
    }

    private void updateScoreBizSrcSaveState(ScoreBizSource scoreBizSource, boolean isSubmit) {
        if (isSubmit ^ scoreBizSource.isLastSaveSubmit()) {
            if (isSubmit) {
                scoreBizSource.setLastSaveSubmit(true);
            } else {
                scoreBizSource.setLastSaveSubmit(false);
            }
        }
        updateAuditInfo(scoreBizSource, false);
        scoreRuleInstanceDao.updateScoreBizSrcForSave(scoreBizSource);
    }

    private static void rebuildTreeFromFE(TagRuleInstance instance) {
        if (!instance.hasChild()) {
            return;
        }
        if (instance.getChildren().size() == 1 && null == instance.getChildren().get(0).getId()) {
            instance.setChildren(null);
            return;
        }
        instance.getChildren().forEach(child -> {
            child.justSetParent(instance);
            child.getDefinition().justSetParent(instance.getDefinition());
        });
        instance.getChildren().forEach(ScoreInstanceServiceImpl::rebuildTreeFromFE);
    }

    @Override
    @Transactional
    public TopTagRuleInstance incrementSave(Long currentScoreBizSrcId, List<TagRuleInstance> feUpdates, boolean isTemp) {
        try {
            if (null == currentScoreBizSrcId) {
                throw new IllegalArgumentException("scoreBizSrcId cannot be empty");
            }
            if (CollectionUtils.isEmpty(feUpdates) && isTemp) {
                return null;
            }
            ScoreBizSource scoreBizSource = findScoreBizSourceOrThrowException(currentScoreBizSrcId);
            return incrementSave(scoreBizSource, feUpdates, isTemp);
        } finally {
            removeStrategy();
        }
    }

    @Override
    @Transactional
    public TopTagRuleInstance incrementSave(String scoreCardCode, String context, List<TagRuleInstance> feUpdates, boolean isTemp) {
        try {
            return incrementSave(scoreCardCode, context, feUpdates, isTemp, CardType.scoreCardCode);
        } finally {
            removeStrategy();
        }
    }

    private TopTagRuleInstance incrementSave(String card, String context, List<TagRuleInstance> feUpdates, boolean isTemp, CardType cardType) {
        if (StringUtils.isEmpty(card) || StringUtils.isEmpty(context)) {
            throw new IllegalArgumentException(cardType + " or context cannot be empty");
        }
        if (CollectionUtils.isEmpty(feUpdates) && isTemp) {
            return null;
        }
        ScoreBizSource scoreBizSource = cardType == CardType.scoreCardCode ?
                findScoreBizSourceOrThrowException(card, context) :
                findScoreBizSourceByCardIdOrThrowException(card, context);
        return incrementSave(scoreBizSource, feUpdates, isTemp);
    }

    @Override
    @Transactional
    public TopTagRuleInstance incrementSaveByCardId(String scoreCardId, String context, List<TagRuleInstance> feUpdates, boolean isTemp) {
        try {
            return incrementSave(scoreCardId, context, feUpdates, isTemp, CardType.scoreCardId);
        } finally {
            removeStrategy();
        }
    }

    @Override
    public boolean isCurrentCardCode(String idProjApply, String scoreCardCode) {
        return false; //todo
    }

    @Override
    public void insertOrUpdateLatestCard(String idProjApply, String scoreCardCode) {
        //todo
    }

    @Override
    public ScoreBizSource findFirstOnlyContext(String[] contexts) {
        if (contexts != null && contexts.length > 0) {
            for (int i = contexts.length - 1; i >= 0; --i) {
                ScoreBizSource scoreBizSource = scoreRuleInstanceDao.findSaveBizSrcOnlyByContext(contexts[i]);
                if (null != scoreBizSource) {
                    return scoreBizSource;
                }
            }
        }
        return null;
    }

    @Override
    public void handleTopTagRuleInstanceDefaultValue(TopTagRuleInstance topTagRuleInstance, boolean isSetupFirstNode) {
        if (isSetupFirstNode) {
            return;
        }
        Map<String, String[]>[] sentimentConfig = scoreTagConfigService
                .findDefaultValueConfig(new String[]{DEFAULT_VALUE_SENTIMENT}, null);
        Map<String, String[]>[] creditRecordConfig = scoreTagConfigService
                .findDefaultValueConfig(new String[]{DEFAULT_VALUE_CREDIT_RECORD}, null);
        // 负面舆情ruleCode
        String[] sentimentRuleCodes = sentimentConfig[0].get(LabelConstants.DEFAULT_VALUE_RULE_CODE);
        // 不良信用记录ruleCode
        String[] creditRecordRuleCodes = creditRecordConfig[0].get(LabelConstants.DEFAULT_VALUE_RULE_CODE);
        // 负面舆情默认值
        String[] sentimentDefaultValue = sentimentConfig[0].get(LabelConstants.DEFAULT_VALUE);
        // 不良信用记录默认值
        String[] creditRecordDefaultValue = creditRecordConfig[0].get(LabelConstants.DEFAULT_VALUE);
        List<TagRuleInstance> list = HierarchyRecord.flat(topTagRuleInstance);
        // 处理负面舆情默认值
        List<TagRuleInstance> sentimentInstanceList = list.stream()
                .filter(top -> ArrayUtils.contains(sentimentRuleCodes, top.getDefinition().getRuleCode()))
                .filter(ins -> Objects.isNull(ins.getValueSource()))
                .filter(TagRuleInstance::isSuit)
                .collect(Collectors.toList());
        handleTagDefDefaultValue(sentimentDefaultValue, list, sentimentInstanceList);

        // 处理不良信用记录默认值
        List<TagRuleInstance> creditRecordInstanceList = list.stream()
                .filter(top -> ArrayUtils.contains(creditRecordRuleCodes, top.getDefinition().getRuleCode()))
                .filter(ins -> Objects.isNull(ins.getValueSource()))
                .filter(TagRuleInstance::isSuit)
                .collect(Collectors.toList());
        handleTagDefDefaultValue(creditRecordDefaultValue, list, creditRecordInstanceList);
    }

    private void handleTagDefDefaultValue(String[] defaultValue, List<TagRuleInstance> list, List<TagRuleInstance> instanceList) {
        if (!CollectionUtils.isEmpty(instanceList)) {
            List<String> codeList = instanceList.stream()
                    .map(i -> i.getDefinition().getRuleCode())
                    .collect(Collectors.toList());
            list.forEach(ins -> {
                if (codeList.contains(ins.getDefinition().getRuleCode())) {
                    ins.setValueSource(defaultValue[0]);
                }
            });
        }
    }

    private TopTagRuleInstance incrementSave(ScoreBizSource scoreBizSource, List<TagRuleInstance> feUpdates, boolean isTemp) {
        ValueScoreStrategy strategy = getStrategy();
        if (isTemp) {
            feUpdates.stream()
                    .filter(instance -> needSave(instance, strategy))
                    .forEach(scoreRuleInstanceDao::updateFromUI);
            updateScoreBizSrcSaveState(scoreBizSource, false);
            return null;
        }
        List<TagRuleInstance> toUpdate = feUpdates.stream()
                .filter(instance -> needSave(instance, strategy)).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(toUpdate)) {
            toUpdate.forEach(scoreRuleInstanceDao::updateInstance);
        }
        updateScoreBizSrcSaveState(scoreBizSource, true);
        return refreshAfterSubmit(scoreBizSource, strategy);
    }

    private void handleRuleInstanceSuitAttribute(String scoreCardCode, RuleContext ruleContext, TopTagRuleInstance topTagRuleInstance) {
        Assert.notNull(scoreCardCode, "handleRuleInstanceSuitAttribute scoreCardCode cannot be empty!");
        Assert.notNull(ruleContext, "handleRuleInstanceSuitAttribute ruleContext cannot be empty!");
        Assert.notNull(topTagRuleInstance, "handleRuleInstanceSuitAttribute topTagRuleInstance cannot be empty!");
        List<String> isSuitTagCodes = ScoreCommonUtil.tagConfigMaps2List(scoreTagConfigService.findConditionalTagIsSuitConfig(scoreCardCode));
        RootModel root = ruleContext.getRoot();
        Assert.notNull(root, "handleRuleInstanceSuitAttribute root cannot be empty!");
        List<TagRuleInstance> instanceList = topTagRuleInstance.getChildren();
        Assert.notNull(instanceList, "handleRuleInstanceSuitAttribute instanceList cannot be empty!");
        List<String> specialScoreCardCodes = Arrays.asList(LabelConstants.FP_ESTATE_CREDIT_DEBT, LabelConstants.AFTER_ESTATE_CREDIT_DEBT);
        // 房地产信用债评分卡只需处理增信方保障主次从标签
        if (specialScoreCardCodes.contains(scoreCardCode)) {
            // 处理增信方保障主次从标签（需要区分房地产信用债评分卡与其他评分卡）
            handleCreditEnhancementGuarantee(isSuitTagCodes, instanceList, root, true);
        } else {
            // 处理抵押保障主次从标签
            handleMortgageProtection(isSuitTagCodes, instanceList, root);
            // 处理底层项目主次从标签
            handleUnderlyingProject(isSuitTagCodes, instanceList, root);
            // 处理增信方保障主次从标签（需要区分房地产信用债评分卡与其他评分卡）
            handleCreditEnhancementGuarantee(isSuitTagCodes, instanceList, root, false);
        }
    }

    // 处理抵押保障主次从标签
    private void handleMortgageProtection(List<String> isSuitTagCodes, List<TagRuleInstance> instanceList, RootModel root) {
        List<TagRuleInstance> suitInstanceList = instanceList.stream()
                .filter(instance -> isSuitTagCodes.get(0).equals(instance.getDefinition().getRuleCode()))
                .collect(Collectors.toList());
        if (root instanceof ProjApply) {
            ProjApply rootModel = (ProjApply) root;
            handleRuleInstanceByIsEmpty(suitInstanceList, CollectionUtils.isEmpty(rootModel.getGuarantyInfoApply()));
        }
        if (root instanceof AfterProjApply) {
            AfterProjApply rootModel = (AfterProjApply) root;
            handleRuleInstanceByIsEmpty(suitInstanceList, CollectionUtils.isEmpty(rootModel.getGuarantyInfoApply()));
        }
    }

    // 处理底层项目主次从标签
    private void handleUnderlyingProject(List<String> isSuitTagCodes, List<TagRuleInstance> instanceList, RootModel root) {
        List<TagRuleInstance> suitInstanceList = instanceList.stream()
                .filter(instance -> isSuitTagCodes.get(1).equals(instance.getDefinition().getRuleCode()))
                .collect(Collectors.toList());
        if (root instanceof ProjApply) {
            ProjApply rootModel = (ProjApply) root;
            boolean isEmpty = false;
            if (CollectionUtils.isEmpty(rootModel.getProjBidRelationApply())) {
                isEmpty = true;
            } else {
                if (rootModel.getProjBidRelationApply().stream().noneMatch(bid -> Objects.nonNull(bid.getBidDetailApply()))) {
                    isEmpty = true;
                }
            }
            handleRuleInstanceByIsEmpty(suitInstanceList, isEmpty);
        }

        if (root instanceof AfterProjApply) {
            AfterProjApply rootModel = (AfterProjApply) root;
            boolean isEmpty = false;
            if (CollectionUtils.isEmpty(rootModel.getProjBidRelationApply())) {
                isEmpty = true;
            } else {
                if (rootModel.getProjBidRelationApply().stream().noneMatch(bid -> Objects.nonNull(bid.getBidDetailApply()))) {
                    isEmpty = true;
                }
            }
            handleRuleInstanceByIsEmpty(suitInstanceList, isEmpty);
        }
    }

    // 处理增信方保障主次从标签
    private void handleCreditEnhancementGuarantee(List<String> isSuitTagCodes, List<TagRuleInstance> instanceList, RootModel root, boolean isSpecial) {
        List<TagRuleInstance> suitInstanceList = instanceList.stream()
                .filter(instance -> isSpecial ? isSuitTagCodes.get(0).equals(instance.getDefinition().getRuleCode())
                        : isSuitTagCodes.get(2).equals(instance.getDefinition().getRuleCode()))
                .collect(Collectors.toList());
        if (root instanceof ProjApply) {
            ProjApply rootModel = (ProjApply) root;
            handleRuleInstanceByIsEmpty(suitInstanceList, CollectionUtils.isEmpty(rootModel.getGuarantorInfoApply()));
        }
        if (root instanceof AfterProjApply) {
            AfterProjApply rootModel = (AfterProjApply) root;
            handleRuleInstanceByIsEmpty(suitInstanceList, CollectionUtils.isEmpty(rootModel.getGuarantorInfoApply()));
        }
    }

    private void handleRuleInstanceByIsEmpty(List<TagRuleInstance> suitInstanceList, boolean isEmpty) {
        if (isEmpty) {
            handleRuleInstanceSuitByRecursion(suitInstanceList, false);
        } else {
            handleRuleInstanceSuitByRecursion(suitInstanceList, true);
        }
    }

    // 递归处理标签适用属性
    private void handleRuleInstanceSuitByRecursion(List<TagRuleInstance> suitInstanceList, boolean isSuit) {
        for (TagRuleInstance instance : suitInstanceList) {
            instance.setSuit(isSuit);
            if (!CollectionUtils.isEmpty(instance.getChildren())) {
                handleRuleInstanceSuitByRecursion(instance.getChildren(), isSuit);
            }
        }
    }
}
