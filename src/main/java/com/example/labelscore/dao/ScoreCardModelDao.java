package com.example.labelscore.dao;

import com.example.labelscore.bean.ScoreCardModel;

/**
 * @Description TODO
 * @Date 2021/4/5 18:07
 * @Created by hdw
 */
public interface ScoreCardModelDao {
    ScoreCardModel findScoreCardModel(String card);

    ScoreCardModel findScoreCardModelById(String card);
}
