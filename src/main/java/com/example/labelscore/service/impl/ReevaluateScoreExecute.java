package com.example.labelscore.service.impl;

import com.example.labelscore.bean.ScoreResult;
import com.example.labelscore.service.ScoreExecute;
import com.example.labelscore.vo.LabelScoreVO;
import com.example.labelscore.vo.ResultVO;
import org.springframework.stereotype.Component;

/**
 * @Description TODO
 * @Date 2021/4/5 15:46
 * @Created by hdw
 */
@Component("ReevaluateScoreExecute")
public class ReevaluateScoreExecute implements ScoreExecute {
    @Override
    public ScoreResult execute(LabelScoreVO labelScoreVO) throws Exception {
        return null;
    }

    @Override
    public ResultVO save(LabelScoreVO labelScoreVO) throws Exception {
        return null;
    }

    @Override
    public ResultVO savePrimaryTag(LabelScoreVO labelScoreVO) throws Exception {
        return null;
    }
}