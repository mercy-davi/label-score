package com.example.labelscore.dao;

import com.example.labelscore.bean.ScoreCardModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Description TODO
 * @Date 2021/4/5 18:07
 * @Created by hdw
 */
@Mapper
public interface ScoreCardModelDao {
    List<ScoreCardModel> findAllScoreCardModels();

    ScoreCardModel findScoreCardModel(String scoreCardCode);

    ScoreCardModel findScoreCardModelById(String scoreCardId);
}
