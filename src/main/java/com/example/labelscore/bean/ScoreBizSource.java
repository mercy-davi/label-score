package com.example.labelscore.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * @Classname ScoreBizSource
 * @Description TODO
 * @Date 2021/3/31
 * @Author hdw
 */
public class ScoreBizSource implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String bizId;
    private String context;
    private ScoreCardModel scoreCardModel;
    private boolean isLastSaveSubmit = false;
    private String createdBy;
    private Date createdDate;
    private String updatedBy;
    private Date updatedDate;

    public ScoreBizSource() {
    }

    public ScoreBizSource(String bizId, String context, ScoreCardModel scoreCardModel) {
        this.bizId = bizId;
        this.context = context;
        this.scoreCardModel = scoreCardModel;
    }

    public ScoreBizSource createACopy(String newContext) {
        ScoreBizSource newCopy = new ScoreBizSource();
        newCopy.context = newContext;
        newCopy.scoreCardModel = this.scoreCardModel;
        newCopy.bizId = this.bizId;
        newCopy.isLastSaveSubmit = false;
        return newCopy;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public ScoreCardModel getScoreCardModel() {
        return scoreCardModel;
    }

    public void setScoreCardModel(ScoreCardModel scoreCardModel) {
        this.scoreCardModel = scoreCardModel;
    }

    public boolean isLastSaveSubmit() {
        return isLastSaveSubmit;
    }

    public void setLastSaveSubmit(boolean lastSaveSubmit) {
        isLastSaveSubmit = lastSaveSubmit;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }
}
