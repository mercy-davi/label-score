package com.example.score.bean;

import com.example.score.service.ScoreFeatures;

/**
 * @Classname ScoreCardModel
 * @Description TODO
 * @Date 2021/3/31
 * @Author hdw
 */
public class ScoreCardModel {
    private String id;
    private String scoreCardCode;
    private String scoreCardName;
    private Integer featureStates = 0;
    private String scoreCardDescription;
    private String modelCode;
    private Integer version;
    private boolean isHistory;
    private transient ScoreFeatures scoreFeatures;

    public ScoreFeatures scoreFeatures() {
        if (null == scoreFeatures) {
            scoreFeatures = new ScoreFeatures(featureStates);
        }
        return scoreFeatures;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getScoreCardCode() {
        return scoreCardCode;
    }

    public void setScoreCardCode(String scoreCardCode) {
        this.scoreCardCode = scoreCardCode;
    }

    public String getScoreCardName() {
        return scoreCardName;
    }

    public void setScoreCardName(String scoreCardName) {
        this.scoreCardName = scoreCardName;
    }

    public Integer getFeatureStates() {
        return featureStates;
    }

    public void setFeatureStates(Integer featureStates) {
        this.featureStates = featureStates;
    }

    public String getScoreCardDescription() {
        return scoreCardDescription;
    }

    public void setScoreCardDescription(String scoreCardDescription) {
        this.scoreCardDescription = scoreCardDescription;
    }

    public String getModelCode() {
        return modelCode;
    }

    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public boolean isHistory() {
        return isHistory;
    }

    public void setHistory(boolean history) {
        isHistory = history;
    }
}
