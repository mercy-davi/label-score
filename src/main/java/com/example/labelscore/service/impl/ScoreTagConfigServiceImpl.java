package com.example.labelscore.service.impl;

import com.example.labelscore.bean.ScoreTagConfig;
import com.example.labelscore.dao.ScoreTagConfigDao;
import com.example.labelscore.service.ScoreTagConfigService;
import com.example.labelscore.util.JsonUtil;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description TODO
 * @Date 2021/4/5 16:09
 * @Created by hdw
 */
@Service
@SuppressWarnings("unchecked")
public class ScoreTagConfigServiceImpl implements ScoreTagConfigService {

    @Autowired
    ScoreTagConfigDao scoreTagConfigDao;

    @Override
    public Map<String, String[]>[] findConditionalTagConfig(String scoreCardCode) {
        return (Map<String, String[]>[])findMapResult(scoreCardCode, String[].class, CONFIG_KEY_CONDITIONAL_EXIST_TAG);
    }

    @Override
    public Map<String, Map<String, Double>>[] findConditionalTagWeightConfig(String scoreCardCode) {
        return (Map<String, Map<String, Double>>[])findMapResult(
                scoreCardCode, Map.class, CONFIG_KEY_CONDITIONAL_EXIST_TAG_WEIGHT, String.class, Double.class);
    }

    @Override
    public Map<String, String>[] findScoreRadarMapConfig(String[] keys, String scoreCardCode) {
        return (Map<String, String>[])findMapResult(String.class, keys, scoreCardCode);
    }

    @Override
    public Map<String, String[]>[] findConditionalTagIsSuitConfig(String scoreCardCode) {
        return (Map<String, String[]>[])findMapResult(scoreCardCode, String[].class, CONFIG_KEY_CONDITIONAL_IS_SUIT);
    }

    @Override
    public Map<String, String[]>[] findAfterNeedUpdateRuleCodeConfig(String[] keys, String scoreCardCode) {
        return (Map<String, String[]>[])findMapResult(String[].class, keys, scoreCardCode);
    }

    @Override
    public Map<String, String[]>[] findDefaultValueConfig(String[] keys, String scoreCardCode) {
        return (Map<String, String[]>[])findMapResult(String[].class, keys, scoreCardCode);
    }

    @Override
    public Map<String, String[]>[] findScorePhaseNoConfig(String[] keys, String scoreCardCode) {
        return (Map<String, String[]>[])findMapResult(String[].class, keys, scoreCardCode);
    }

    @Override
    public Map<String, ?>[] findConfig(Class<?> valueClass, String[] keys, String scoreCardCode) {
        return findMapResult(valueClass, keys, scoreCardCode);
    }

    private Map<String,?>[] findMapResult(String scoreCardCode, Class<?> valueClass, String key, Class<?>... valueParamType) {
        if (StringUtils.isBlank(scoreCardCode)) {
            throw new IllegalArgumentException("score card code cannot be empty");
        }
        ScoreTagConfig[] scoreTagConfigs = scoreTagConfigDao.findManyAtCardLevel(scoreCardCode, key);
        if (scoreTagConfigs.length == 0) {
            return new HashMap[0];
        }
        Map<String,?>[] maps = new HashMap[scoreTagConfigs.length];
        for (int i = 0; i < scoreTagConfigs.length; ++i) {
            ScoreTagConfig config = scoreTagConfigs[i];
            if (StringUtils.isBlank(config.getConfigValue())) {
                throw new IllegalStateException(config.getConfigKey() + "config value is blank");
            }
            if (valueParamType.length == 0) {
                maps[i] = JsonUtil.jsonToMap(config.getConfigValue(), String.class, valueClass);
            } else {
                maps[i] = JsonUtil.jsonToMap(config.getConfigValue(), String.class, valueClass, valueParamType);
            }
        }
        return maps;
    }

    private Map<String,?>[] findMapResult(Class<?> valueClass, String[] keys, String scoreCardCode, Class<?>... valueParamType) {
        if (ArrayUtils.isEmpty(keys)) {
            throw new IllegalArgumentException("config keys cannot be empty");
        }
        ScoreTagConfig[] scoreTagConfigs = scoreTagConfigDao.findListByConfigKey(keys, scoreCardCode);
        if (scoreTagConfigs.length == 0) {
            return new HashMap[0];
        }
        Map<String,?>[] maps = new HashMap[scoreTagConfigs.length];
        for (int i = 0; i < scoreTagConfigs.length; i++) {
            for (ScoreTagConfig config : scoreTagConfigs) {
                if (StringUtils.isBlank(config.getConfigValue())) {
                    throw new IllegalStateException(config.getConfigKey() + "config value is blank");
                }
                if (!keys[i].equals(config.getConfigKey())) {
                    continue;
                }
                if (valueParamType.length == 0) {
                    maps[i] = JsonUtil.jsonToMap(config.getConfigValue(), String.class, valueClass);
                } else {
                    maps[i] = JsonUtil.jsonToMap(config.getConfigValue(), String.class, valueClass, valueParamType);
                }
            }
        }
        return maps;
    }
}
