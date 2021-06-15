package com.example.score.service;

import com.example.score.bean.ScoreBizSource;
import com.example.score.bean.TagRuleInstance;
import com.example.score.bean.TopTagRuleInstance;

import java.util.List;

/**
 * @Description TODO
 * @Date 2021/4/4 22:26
 * @Created by hdw
 */
public interface ScoreInstanceService {

    ScoreBizSource searchBizSourceByContext(String context, String scoreCardCode);

    /**
     * @param contextLike contextLike without % as prefix or suffix
     * @param scoreCardCode scoreCardCode if null, will search all otherwise search those of the specific scoreCardCode
     * @return
     */
    List<ScoreBizSource> searchContextLike(String contextLike, String scoreCardCode);

    /**
     * @param contextLike contextLike without % as prefix or suffix
     * @param scoreCardId scoreCardId if null, will search all otherwise search those of the specific scoreCardId
     * @return
     */
    List<ScoreBizSource> searchContextLikeById(String contextLike, String scoreCardId);

    ScoreBizSource findFirstFromTail2Top(String scoreCardCode, String[] contexts);

    ScoreBizSource findFirstFromTail2TopByCardId(String scoreCardId, String[] contexts);

    boolean isLastSaveSubmit(Long scoreBizSrcId);

    boolean isLastSaveSubmit(String scoreCardCode, String context);

    boolean isLastSaveSubmitByCardId(String scoreCardId, String context);

    /**
     * @param maxScoreTreeLevel {@link ScoreTreeLevel scoreTreeLevel}
     * @see  ScoreTreeLevel
     */
    TopTagRuleInstance viewSolid(Long scoreBizSrcId, int maxScoreTreeLevel);

    /**
     * @param maxScoreTreeLevel {@link ScoreTreeLevel scoreTreeLevel}
     * @see  ScoreTreeLevel
     */
    TopTagRuleInstance viewSolid(String scoreCardCode, String context, int maxScoreTreeLevel);

    /**
     * @param maxScoreTreeLevel {@link ScoreTreeLevel scoreTreeLevel}
     * @see  ScoreTreeLevel
     */
    TopTagRuleInstance viewSolidByCardId(String scoreCardId, String context, int maxScoreTreeLevel);

    TopTagRuleInstance viewSolidComplete(Long scoreBizSrcId);
    TopTagRuleInstance viewSolidComplete(String scoreCardCode, String context);
    TopTagRuleInstance viewSolidCompleteByCardId(String scoreCardId, String context);

    /**
     * @param averageSources all of the data are from others. averageSources.length = 1 + N =>
     *                       averageSources[0]->columnNames, averageSources[1...N]->scoreBizSrcIds
     */
    TopTagRuleInstance viewSolidAverage(Object[][] averageSources, int maxScoreTreeLevel, boolean canAvgEmpty);

    /**
     * @param scoreCardCode the (scoreCardCode, context) pair, only one scoreCardCode
     * @param averageSources all of the data are from others. averageSources.length = 1 + N =>
     *                       averageSources[0]->columnNames, averageSources[1]->contexts,
     *                       averageSources[2]->contexts...averageSources[N]->contexts
     */
    TopTagRuleInstance viewSolidAverage(String scoreCardCode, String[][] averageSources, int maxScoreTreeLevel, boolean canAvgEmpty);

    /**
     * @param scoreCardId the (scoreCardId, context) pair, only one scoreCardId
     * @param averageSources all of the data are from others. averageSources.length = 1 + N =>
     *                       averageSources[0]->columnNames, averageSources[1]->contexts,
     *                       averageSources[2]->contexts...averageSources[N]->contexts
     */
    TopTagRuleInstance viewSolidAverageByCardId(String scoreCardId, String[][] averageSources, int maxScoreTreeLevel, boolean canAvgEmpty);

    TopTagRuleInstance viewEmpty(String scoreCardCode, String[][] averageSources, int maxScoreTreeLevel, boolean canAvgEmpty);

    TopTagRuleInstance viewEmptyByCardId(String scoreCardId, String[][] averageSources, int maxScoreTreeLevel, boolean canAvgEmpty);

    /**
     * @param averageSources other ScoreBizSrc, averageSources.length = 1 + N =>
     *                       averageSources[0]->columnNames, averageSources[1...N]->scoreBizSrcIds
     */
    TopTagRuleInstance openForWriteAlsoAverage(Long currentScoreBizSrcId, Object[][] averageSources, int maxScoreTreeLevel, boolean canAvgEmpty);

    /**
     * @param averageSources other ScoreBizSrc, averageSources.length = 1 + 2N =>averageSources[0]->columnNames,
     *                       averageSources[1]->scoreCardCodes, averageSources[2]->contexts,
     *                       averageSources[3]->scoreCardCodes, averageSources[4]->contexts
     *                       ...averageSources[2N-1]->scoreCardCodes, averageSources[2N]->contexts
     */
    TopTagRuleInstance openForWriteAlsoAverage(String bizId, String scoreCardCode, String context, String[][] averageSources, int maxScoreTreeLevel, boolean canAvgEmpty);

    /**
     * @param averageSources other ScoreBizSrc, averageSources.length = 1 + 2N =>averageSources[0]->columnNames,
     *                       averageSources[1]->scoreCardIds, averageSources[2]->contexts,
     *                       averageSources[3]->scoreCardIds, averageSources[4]->contexts
     *                       ...averageSources[2N-1]->scoreCardIds, averageSources[2N]->contexts
     */
    TopTagRuleInstance openForWriteAlsoAverageByCardId(String bizId, String scoreCardId, String context, String[][] averageSources, int maxScoreTreeLevel, boolean canAvgEmpty);

    TopTagRuleInstance copyIfNotExistOrOpenAlsoAverage(ScoreBizSource copyFrom, String context, String[][] averageSources, int maxScoreTreeLevel, boolean canAvgEmpty);

    TopTagRuleInstance openCompleteForWrite(Long currentScoreBizSrcId);

    TopTagRuleInstance openCompleteForWrite(String bizId, String scoreCardCode, String context);

    TopTagRuleInstance openCompleteForWriteByCardId(String bizId, String scoreCardId, String context);

    TopTagRuleInstance createACopyIfNotExistOrOpen(Long existedScoreBizSrcId, String currentContext);

    TopTagRuleInstance createACopyIfNotExistOrOpen(String bizId, Long existedScoreBizSrcId, String currentContext);

    TopTagRuleInstance createACopyIfNotExistOrOpen(String scoreCardCode, String existedContext, String currentContext);

    TopTagRuleInstance createACopyIfNotExistOrOpenByCardId(String scoreCardId, String existedContext, String currentContext);

    TopTagRuleInstance createACopyIfNotExistOrOpen(Long existedScoreBizSrcId, String currentContext, boolean isLastSaveStateCopiedIfNotExist);

    TopTagRuleInstance createACopyIfNotExistOrOpen(String scoreCardCode, String existedContext, String currentContext, boolean isLastSaveStateCopiedIfNotExist);

    TopTagRuleInstance createACopyIfNotExistOrOpenByCardId(String scoreCardId, String existedContext, String currentContext, boolean isLastSaveStateCopiedIfNotExist);

    TopTagRuleInstance eval(Long scoreBizSrcId, TopTagRuleInstance tree);

//    TopTagRuleInstance eval(String bizId, String scoreCardCode, String context);
//    TopTagRuleInstance eval(String bizId, String scoreCardCode, String contextKey, Object contextValue);
//    TopTagRuleInstance eval(String bizId, String scoreCardCode, String contextKey1, Object contextValue1,
//                            String contextKey2, Object contextValue2);
//    TopTagRuleInstance eval(String bizId, String scoreCardCode, String contextKey1, Object contextValue1,
//                            String contextKey2, Object contextValue2, String contextKey3, Object contextValue3);
//    TopTagRuleInstance eval(String bizId, String scoreCardCode, Map<String, Object> contextMap);
//
//    TopTagRuleInstance evalByCardId(String bizId, String scoreCardId, String context);
//    TopTagRuleInstance evalByCardId(String bizId, String scoreCardId, String contextKey, Object contextValue);
//    TopTagRuleInstance evalByCardId(String bizId, String scoreCardId, String contextKey1, Object contextValue1,
//                            String contextKey2, Object contextValue2);
//    TopTagRuleInstance evalByCardId(String bizId, String scoreCardId, String contextKey1, Object contextValue1,
//                            String contextKey2, Object contextValue2, String contextKey3, Object contextValue3);
//    TopTagRuleInstance evalByCardId(String bizId, String scoreCardId, Map<String, Object> contextMap);

    TopTagRuleInstance fullSave(Long currentScoreBizSrcId, TopTagRuleInstance tree, boolean isTemp);
    TopTagRuleInstance fullSave(String scoreCardCode, String context, TopTagRuleInstance tree, boolean isTemp);
    TopTagRuleInstance fullSaveByCardId(String scoreCardId, String context, TopTagRuleInstance tree, boolean isTemp);

    TopTagRuleInstance incrementSave(Long currentScoreBizSrcId, List<TagRuleInstance> feUpdates, boolean isTemp);
    TopTagRuleInstance incrementSave(String scoreCardCode, String context, List<TagRuleInstance> feUpdates, boolean isTemp);
    TopTagRuleInstance incrementSaveByCardId(String scoreCardId, String context, List<TagRuleInstance> feUpdates, boolean isTemp);



    boolean isCurrentCardCode(String idProjApply, String scoreCardCode);

    void insertOrUpdateLatestCard(String idProjApply, String scoreCardCode);

    /**
     * 仅根据context查询BizSrc
     * @param contexts BizSrcContexts
     * @return BizSrc
     */
    ScoreBizSource findFirstOnlyContext(String[] contexts);

    /**
     * 处理标签树中新增标签，无值时给默认值
     * @param topTagRuleInstance 标签树
     * @param isSetupFirstNode 是否立项第一岗
     */
    void handleTopTagRuleInstanceDefaultValue(TopTagRuleInstance topTagRuleInstance, boolean isSetupFirstNode);
}
