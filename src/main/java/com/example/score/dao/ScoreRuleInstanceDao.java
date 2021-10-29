package com.example.score.dao;

import com.example.score.bean.ScoreBizSource;
import com.example.score.bean.TagRuleInstance;
import com.example.score.bean.TagRuleInstanceWithDef;
import com.example.score.util.Tuple2;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description TODO
 * @Date 2021/4/5 18:06
 * @Created by hdw
 */
@Mapper
public interface ScoreRuleInstanceDao {

    Long createNewSet();

    ScoreBizSource findBizSrcById(@Param("id") Long id);

    ScoreBizSource findBizSrcByIdWithScoreCard(@Param("id") Long id);

    ScoreBizSource findBizSrc(@Param("scoreCardId") String scoreCardId, @Param("context") String context);

    ScoreBizSource findBizSrcWithScoreCard(@Param("scoreCardId") String scoreCardId, @Param("context") String context);

    ScoreBizSource findBizSrcByCode(@Param("scoreCardCode") String scoreCardCode, @Param("context") String context);

    ScoreBizSource findBizSrcByCodeWithScoreCard(@Param("scoreCardCode") String scoreCardCode, @Param("context") String context);

    List<ScoreBizSource> findAllLikeByCode(@Param("contextLike") String contextLike, @Param("scoreCardCode") String scoreCardCode);

    List<ScoreBizSource> findAllLike(@Param("contextLike") String contextLike, @Param("scoreCardId") String scoreCardId);

    List<TagRuleInstance> findByBizSrcIdWithDef(@Param("bizSrcId") Long bizSrcId);

    /**
     * @deprecated replace by {@link #findByBizSrcIdAndLevel}
     */
    @Deprecated
    List<TagRuleInstanceWithDef> findSolidInstance(@Param("bizSrcId") Long bizSrcId);

    int insertBizSrc(ScoreBizSource scoreBizSource);

    int updateScoreBizSrcForSave(ScoreBizSource scoreBizSource);

    Boolean getScoreBizSrcSaveState(@Param("bizSrcId") Long bizSrcId);

    Boolean getScoreBizSrcSaveStateByCardCode(@Param("scoreCardCode") String scoreCardCode, @Param("context") String context);

    Boolean getScoreBizSrcSaveStateByCardId(@Param("scoreCardId") String scoreCardId, @Param("context") String context);

    int insertList(List<TagRuleInstance> list);

    @Deprecated
    int updateParentAsScoreChange(List<TagRuleInstance> list);

    List<TagRuleInstance> findByBizSrcIdAndLevel(@Param("bizSrcId") Long bizSrcId, @Param("maxLevel") int maxLevel);

    List<Double> findAverage(@Param("bizSrcIds") Long[] bizSrcIds, @Param("maxLevel") int maxLevel);

    List<Double> findAverageByCardCode(@Param("scoreCardCodeContextTuple") Tuple2<String, String>[] scoreCardCodeContextTuple, @Param("maxLevel") int maxLevel);

    List<Double> findAverageByCardId(@Param("scoreCardIdContextTuple") Tuple2<String, String>[] scoreCardCodeContextTuple, @Param("maxLevel") int maxLevel);

    List<Double> findAverageByOneCardCode(@Param("scoreCardCode") String scoreCardCode, @Param("contexts") String[] contexts, @Param("maxLevel") int maxLevel);

    List<Double> findAverageByOneCardId(@Param("scoreCardId") String scoreCardId, @Param("contexts") String[] contexts, @Param("maxLevel") int maxLevel);

    void updateInstance(TagRuleInstance updated);

    void deleteInstances(@Param("deleted") List<TagRuleInstance> deleted);

    void deleteInstanceByRange(@Param("lastMaxId") Long lastMaxId, @Param("currentMaxId") Long currentMaxId);

    void updateFromUI(TagRuleInstance updated);

    void updateParentCauseByUI(TagRuleInstance updated);

    void updatePrimaryTagScore(TagRuleInstance ruleInstance);

    ScoreBizSource findSaveBizSrcOnlyByContext(@Param("context") String context);

    String[] findRuleCodeByCardCode(@Param("scoreCardCode") String scoreCardCode);

    String findDisplayNameByCode(@Param("ruleCode") String ruleCode);
}
