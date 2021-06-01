package com.example.labelscore.service.impl;

import com.example.labelscore.bean.ScoreResult;
import com.example.labelscore.service.ScoreExecute;
import com.example.labelscore.vo.LabelScoreVO;
import com.example.labelscore.vo.ResultVO;
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
