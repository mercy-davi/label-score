package com.example.score.service.impl;

import com.example.score.bean.ScoreResult;
import com.example.score.service.ScoreExecute;
import com.example.score.vo.LabelScoreVO;
import com.example.score.vo.ResultVO;
import org.springframework.stereotype.Component;

/**
 * @Description TODO
 * @Date 2021/4/5 15:37
 * @Created by hdw
 */
@Component("DecisionScoreExecute")
public class DecisionScoreExecute implements ScoreExecute {

    @Override
    public ScoreResult execute(LabelScoreVO labelScoreVO) throws Exception {
        return null;
    }

    @Override
    public ResultVO save(LabelScoreVO labelScoreVO) throws Exception {
        throw new IllegalStateException("decision do not need to save label");
    }

    @Override
    public ResultVO savePrimaryTag(LabelScoreVO labelScoreVO) throws Exception {
        throw new IllegalStateException("decision do not need to save label");
    }
}
