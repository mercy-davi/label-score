package com.example.score.service;

import com.example.score.constants.LabelConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @Description TODO
 * @Date 2021/4/5 9:45
 * @Created by hdw
 */
@Component
public class ScoreExecuteFactory {

    @Autowired
    @Qualifier("SetupScoreExecute")
    private ScoreExecute setupScoreExecute;

    @Autowired
    @Qualifier("VotingScoreExecute")
    private ScoreExecute votingScoreExecute;

    @Autowired
    @Qualifier("DecisionScoreExecute")
    private ScoreExecute decisionScoreExecute;

    @Autowired
    @Qualifier("ReevaluateScoreExecute")
    private ScoreExecute reevaluateScoreExecute;

    public ScoreExecute getScoreExecute(String scoreType) {
        switch (scoreType) {
            case LabelConstants.SCORE_TYPE_SETUP:
                return setupScoreExecute;
            case LabelConstants.SCORE_TYPE_VOTING:
                return votingScoreExecute;
            case LabelConstants.SCORE_TYPE_DECISION:
                return decisionScoreExecute;
            case LabelConstants.SCORE_TYPE_REEVALUATE:
                return reevaluateScoreExecute;
            default:
                throw new IllegalArgumentException("wrong scoreType, please confirm!");
        }
    }
}
