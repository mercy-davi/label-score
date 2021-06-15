package com.example.score.dao;

import com.example.score.bean.TagRuleDef;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Classname ScoreRuleDefDao
 * @Date 2021/5/20
 * @Author hdw
 */
@Mapper
public interface ScoreRuleDefDao {
    int countByScoreCardCode(String scoreCardCode);

    /**
     * find the latest rule, and history records are excluded
     *
     * @param scoreCardCode scoreCardCode
     * @return List<TagRuleDef>
     */
    List<TagRuleDef> findByScoreCardCode(String scoreCardCode);

    List<TagRuleDef> findByScoreCardCodeAndLevel(@Param("scoreCardCode") String scoreCardCode, @Param("maxLevel") int maxLevel);

    /**
     * find the latest rule, and history records are excluded
     *
     * @param scoreCardModelId scoreCardModelId
     * @return List<TagRuleDef>
     */
    List<TagRuleDef> findByScoreCardModelId(String scoreCardModelId);

    List<TagRuleDef> findByScoreCardModelIdAndLevel(@Param("scoreCardModelId") String scoreCardModelId, @Param("maxLevel") int maxLevel);
}
