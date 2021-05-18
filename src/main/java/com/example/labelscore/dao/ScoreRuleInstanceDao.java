package com.example.labelscore.dao;

import com.example.labelscore.bean.ScoreBizSource;
import com.example.labelscore.bean.TagRuleInstance;
import com.example.labelscore.util.Tuple2;

import java.util.List;

/**
 * @Description TODO
 * @Date 2021/4/5 18:06
 * @Created by hdw
 */
public interface ScoreRuleInstanceDao {
    ScoreBizSource findBizSrcByIdWithScoreCard(Long scoreBizSrcId);

    ScoreBizSource findBizSrcByCodeWithScoreCard(String scoreCardCode, String context);

    ScoreBizSource findBizSrc(String scoreCardId, String context);

    ScoreBizSource findBizSrcByCode(String scoreCardCode, String context);

    List<ScoreBizSource> findAllLikeByCode(String contextLike, String scoreCardCode);

    List<ScoreBizSource> findAllLike(String contextLike, String scoreCardId);

    ScoreBizSource finBizSrc(String card, String context);

    Boolean getScoreBizSrcSaveState(Long scoreBizSrcId);

    Boolean getScoreBizSrcSaveStateByCardCode(String scoreCardCode, String context);

    Boolean getScoreBizSrcSaveStateByCardId(String scoreCardId, String context);

    List<TagRuleInstance> findByBizSrcIdAndLevel(long id, int maxScoreTreeLevel);

    List<TagRuleInstance> findByBizSrcIdWithDef(long id);

    List<Double> findAverage(Long[] averageSource, int maxScoreTreeLevel);

    List<Double> findAverageByOneCardCode(String card, String[] averageSource, int maxScoreTreeLevel);

    List<Double> findAverageByOneCardId(String card, String[] averageSource, int maxScoreTreeLevel);

    Long createNewSet();

    void insertBizSrc(ScoreBizSource scoreBizSource);

    void insertList(List<TagRuleInstance> list);

    List<Double> findAverageByCardCode(Tuple2<String, String>[] tuple2s, int maxScoreTreeLevel);

    List<Double> findAverageByCardId(Tuple2<String, String>[] tuple2s, int maxScoreTreeLevel);

    ScoreBizSource findBizSrcById(Long id);

    void updateScoreBizSrcForSave(ScoreBizSource scoreBizSource);

    void deleteInstanceByRange(Long lastMaxId, Long currentMaxId);

    void updateInstance(TagRuleInstance instance);

    void updateFromUI(TagRuleInstance ruleInstance);

    void updateParentCauseByUI(TagRuleInstance ruleInstance);

    ScoreBizSource findSaveBizSrcOnlyByContext(String context);
}
