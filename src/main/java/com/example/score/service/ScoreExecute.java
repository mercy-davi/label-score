package com.example.score.service;

import com.example.score.bean.ScoreResult;
import com.example.score.vo.LabelScoreVO;
import com.example.score.vo.ResultVO;

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
