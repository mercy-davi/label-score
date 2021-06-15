package com.example.score.service;

import com.example.score.bean.TagRuleDef;

import java.util.List;

/**
 * @Description TODO
 * @Date 2021/4/4 22:22
 * @Created by hdw
 */
public interface ScoreDefService {
    List<TagRuleDef> findScoreRulesByCode(String scoreCardCode);

    List<TagRuleDef> findScoreRulesById(String scoreCardModelId);

    int countByScoreCardCode(String scoreCardCode);

    List<TagRuleDef> findByScoreCardCodeAndLevel(String scoreCardCode, int maxLevel, boolean needBuildTree);
}
