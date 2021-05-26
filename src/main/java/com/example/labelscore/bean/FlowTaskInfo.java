package com.example.labelscore.bean;

import java.util.Date;

/**
 * @Classname FlowTaskInfo
 * @Date 2021/5/20
 * @Author hdw
 */
public class FlowTaskInfo {
    private String id;

    private String objectNo;

    private String objectType;

    private String phaseNp;

    private Date beginTime;

    private Date endTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObjectNo() {
        return objectNo;
    }

    public void setObjectNo(String objectNo) {
        this.objectNo = objectNo;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getPhaseNp() {
        return phaseNp;
    }

    public void setPhaseNp(String phaseNp) {
        this.phaseNp = phaseNp;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
