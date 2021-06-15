package com.example.score.cache;

import com.example.score.bean.TagRuleDef;
import com.example.score.constants.LabelConstants;
import com.example.score.dao.ScoreRuleDefDao;
import com.example.score.service.ScoreTagConfigService;
import com.example.score.service.ScoreTreeLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.example.score.service.ScoreTagConfigService.CONFIG_KEY_SCORE_PHASE_NO;

/**
 * @Description TODO
 * @Date 2021/4/3 11:16
 * @Created by hdw
 */
@Component
public class ScoreLocalCache {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScoreLocalCache.class);

    private final Map<String, Object> cache = new ConcurrentHashMap<>();
    public static final String CACHE_MARK = "_";

    @Autowired
    private ScoreRuleDefDao scoreRuleDefDao;

    @Autowired
    private ScoreTagConfigService scoreTagConfigService;

    @PostConstruct
    public void init() {
        try {
            // 缓存评分卡
            // 通用版所有标签
            List<TagRuleDef> threeLevel = scoreRuleDefDao.findByScoreCardCode(LabelConstants.FP_THREE_LEVEL_LABEL_EVALUATION);
            cache.put(LabelConstants.FP_THREE_LEVEL_LABEL_EVALUATION, threeLevel);
            // 通用版一级标签
            List<TagRuleDef> threeLevel1 = scoreRuleDefDao.findByScoreCardCodeAndLevel(LabelConstants.FP_THREE_LEVEL_LABEL_EVALUATION, ScoreTreeLevel.PRIMARY_TAG);
            cache.put(LabelConstants.FP_THREE_LEVEL_LABEL_EVALUATION + CACHE_MARK + ScoreTreeLevel.PRIMARY_TAG, threeLevel1);
            // 通用版二级标签
            List<TagRuleDef> threeLevel2 = scoreRuleDefDao.findByScoreCardCodeAndLevel(LabelConstants.FP_THREE_LEVEL_LABEL_EVALUATION, ScoreTreeLevel.SECONDARY_TAG);
            cache.put(LabelConstants.FP_THREE_LEVEL_LABEL_EVALUATION + CACHE_MARK + ScoreTreeLevel.SECONDARY_TAG, threeLevel2);
            // 通用版三级标签
            List<TagRuleDef> threeLevel3 = scoreRuleDefDao.findByScoreCardCodeAndLevel(LabelConstants.FP_THREE_LEVEL_LABEL_EVALUATION, ScoreTreeLevel.TERTIARY_TAG);
            cache.put(LabelConstants.FP_THREE_LEVEL_LABEL_EVALUATION + CACHE_MARK + ScoreTreeLevel.TERTIARY_TAG, threeLevel3);

            // 缓存标签配置
            cache.put(CONFIG_KEY_SCORE_PHASE_NO, scoreTagConfigService.findScorePhaseNoConfig(new String[]{CONFIG_KEY_SCORE_PHASE_NO}, null));
        } catch (Exception e) {
            LOGGER.error("cannot init score local cache", e);
        }
    }

    public <T> T get(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        Object value = cache.get(key);
        if (null == value) {
            return null;
        }
        T t = null;
        try {
            t = (T) value;
        } catch (ClassCastException e) {
            LOGGER.error("score local cache cast exception!", e);
        }
        return t;
    }

    public <T> boolean put(String key, T t) {
        if (StringUtils.isEmpty(key)) {
            return false;
        }
        cache.put(key, t);
        return true;
    }
}
