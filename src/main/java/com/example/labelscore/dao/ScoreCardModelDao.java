package com.example.labelscore.dao;

import com.example.labelscore.bean.ScoreCardModel;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description TODO
 * @Date 2021/4/5 18:07
 * @Created by hdw
 */
@Mapper
public interface ScoreCardModelDao {
    ScoreCardModel findScoreCardModel(String card);

    ScoreCardModel findScoreCardModelById(String card);
}
