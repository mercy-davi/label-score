package com.example.labelscore.dao;

import com.example.labelscore.bean.ScoreTagConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description TODO
 * @Date 2021/4/11 18:17
 * @Created by hdw
 */
//OrderByDefAndKey
@Mapper
public interface ScoreTagConfigDao {
    List<ScoreTagConfig> findCardAllConfig(@Param("scoreCardCode") String scoreCardCode);
    List<ScoreTagConfig> findTagAllConfig(@Param("ruleDefId") String ruleDefId);
    ScoreTagConfig findOneAtCardLevel(@Param("scoreCardCode") String scoreCardCode, @Param("key") String key);
    ScoreTagConfig findOneByConfigKey(@Param("key") String key);
    ScoreTagConfig[] findManyAtCardLevel(@Param("scoreCardCode") String scoreCardCode, @Param("key") String key);
    List<ScoreTagConfig> findListAtCardLevel(@Param("scoreCardCode") String scoreCardCode, @Param("keys") String[] keys);
    ScoreTagConfig[] findListByConfigKey(@Param("keys") String[] keys, @Param("scoreCardCode") String scoreCardCode);
    int updateConfigValue(@Param("scoreCardCode") String scoreCardCode, @Param("key") String key, @Param("value") String value);
}
