package com.example.labelscore.service;

import com.example.labelscore.bean.ScoreResult;
import com.example.labelscore.vo.LabelScoreVO;
import com.example.labelscore.vo.ResultVO;

/**
 * @Description TODO
 * @Date 2021/4/5 9:42
 * @Created by hdw
 */
public interface ScoreExecute {
    ScoreResult execute(LabelScoreVO labelScoreVO) throws Exception;

    ResultVO save(LabelScoreVO labelScoreVO) throws Exception;

    ResultVO savePrimaryTag(LabelScoreVO labelScoreVO) throws Exception;
}
