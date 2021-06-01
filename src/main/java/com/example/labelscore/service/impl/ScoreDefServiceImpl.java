package com.example.labelscore.service.impl;

import com.example.labelscore.bean.HierarchyRecord;
import com.example.labelscore.bean.TagRuleDef;
import com.example.labelscore.dao.ScoreRuleDefDao;
import com.example.labelscore.service.ScoreDefService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @Description TODO
 * @Date 2021/4/5 15:50
 * @Created by hdw
 */
public class ScoreDefServiceImpl implements ScoreDefService {

    @Autowired
    ScoreRuleDefDao scoreRuleDefDao;

//    @Autowired
//    private ScoreLocalCache cache;

    @Override
    public List<TagRuleDef> findScoreRulesByCode(String scoreCardCode) {
//        List<TagRuleDef> tagRuleDefs = cache.get(scoreCardCode);
//        if (null == tagRuleDefs) {
//            tagRuleDefs = scoreRuleDefDao.findByScoreCardCode(scoreCardCode);
//            cache.put(scoreCardCode, tagRuleDefs);
//        }
        List<TagRuleDef> tagRuleDefs = scoreRuleDefDao.findByScoreCardCode(scoreCardCode);
        if (CollectionUtils.isEmpty(tagRuleDefs)) {
            throw new IllegalStateException("scoreCardCode " + scoreCardCode + " has not yet been defined with score rule");
        }
        HierarchyRecord.buildTree(tagRuleDefs);
        return tagRuleDefs;
    }

    @Override
    public List<TagRuleDef> findScoreRulesById(String scoreCardModelId) {
        List<TagRuleDef> tagRuleDefs = scoreRuleDefDao.findByScoreCardModelId(scoreCardModelId);
        if (CollectionUtils.isEmpty(tagRuleDefs)) {
            throw new IllegalStateException("scoreCardModelId " + scoreCardModelId + " has not yet been defined with score rule");
        }
        HierarchyRecord.buildTree(tagRuleDefs);
        return tagRuleDefs;
    }

    @Override
    public int countByScoreCardCode(String scoreCardCode) {
        return scoreRuleDefDao.countByScoreCardCode(scoreCardCode);
    }

    @Override
    public List<TagRuleDef> findByScoreCardCodeAndLevel(String scoreCardCode, int maxLevel, boolean needBuildTree) {
//        String cacheKey = scoreCardCode + CACHE_MARK + maxLevel;
//        List<TagRuleDef> tagRuleDefs = cache.get(cacheKey);
//        if (null == tagRuleDefs) {
//            tagRuleDefs = scoreRuleDefDao.findByScoreCardCodeAndLevel(scoreCardCode, maxLevel);
//            cache.put(cacheKey, tagRuleDefs);
//        }
        List<TagRuleDef> tagRuleDefs = scoreRuleDefDao.findByScoreCardCodeAndLevel(scoreCardCode, maxLevel);
        if (CollectionUtils.isEmpty(tagRuleDefs)) {
            throw new IllegalArgumentException("cannot find score rule defs for scoreCardCode:" +
                    scoreCardCode);
        }
        if (needBuildTree) {
            HierarchyRecord.buildTree(tagRuleDefs);
        }
        return tagRuleDefs;
    }
}
