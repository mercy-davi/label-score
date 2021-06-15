package com.example.score.service.impl;

import com.example.score.bean.ScoreResult;
import com.example.score.service.ScoreExecute;
import com.example.score.vo.LabelScoreVO;
import com.example.score.vo.ResultVO;
import org.springframework.stereotype.Component;

/**
 * @Description TODO
 * @Date 2021/4/5 16:07
 * @Created by hdw
 */
@Component("VotingScoreExecute")
public class VotingScoreExecute implements ScoreExecute {
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
