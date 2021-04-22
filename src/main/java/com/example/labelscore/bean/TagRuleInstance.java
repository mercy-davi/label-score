package com.example.labelscore.bean;

import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Classname TagRuleInstance
 * @Description TODO
 * @Date 2021/3/31
 * @Author hdw
 */
public class TagRuleInstance implements HierarchyRecord<Long>, Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long parentInstanceId;
    private String scoreRuleDefId;
    private Double autoScoreValue;
    private String valueSource;
    private Double manualScore;
    private transient Double latestPreHandlerScore;
    private String manualScoreReason;
    private boolean isSuit = true;

    private transient TagRuleInstance parentInstance;
    private transient List<TagRuleInstance> children;
    private transient TagRuleDef definition;
    private int level;

    private transient Object extraProp;
    private transient int rwState;

    @Override
    public int level() {
        return level;
    }

    @Override
    public Long code() {
        return id;
    }

    @Override
    public Long getParentCode() {
        return null != parentInstance ? parentInstance.getId() : parentInstanceId;
    }

    @Override
    public void parent(HierarchyRecord<Long> parent) {
        this.parentInstance = (TagRuleInstance) parent;
        this.parentInstance.addChildrenNoCheckDuplicate(this);
    }

    @Override
    public TagRuleInstance parent() {
        return this.parentInstance;
    }

    @Override
    public void justSetParent(HierarchyRecord<Long> parent) {
        this.parentInstance = (TagRuleInstance) parent;
    }

    @Override
    public boolean hasChild() {
        return !CollectionUtils.isEmpty(children);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentInstanceId() {
        return getParentCode();
    }

    public void setParentInstanceId(Long parentInstanceId) {
        this.parentInstanceId = parentInstanceId;
    }

    public String getScoreRuleDefId() {
        return null != definition ? definition.getId() : scoreRuleDefId;
    }

    public void setScoreRuleDefId(String scoreRuleDefId) {
        this.scoreRuleDefId = scoreRuleDefId;
    }

    public Double getAutoScoreValue() {
        return autoScoreValue;
    }

    public void setAutoScoreValue(Double autoScoreValue) {
        this.autoScoreValue = autoScoreValue;
    }

    @Override
    public List<TagRuleInstance> getChildren() {
        return children;
    }

    public void setChildren(List<TagRuleInstance> children) {
        this.children = children;
    }

    private void addChildrenNoCheckDuplicate(TagRuleInstance child) {
        if (null == children) {
            children = new ArrayList<>();
        }
        children.add(child);
    }

    public TagRuleDef getDefinition() {
        return definition;
    }

    public void setDefinition(TagRuleDef definition) {
        this.definition = definition;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getValueSource() {
        return valueSource;
    }

    public void setValueSource(String valueSource) {
        this.valueSource = valueSource;
    }

    public Double getManualScore() {
        return manualScore;
    }

    public void setManualScore(Double manualScore) {
        this.manualScore = manualScore;
    }

    public String getManualScoreReason() {
        return manualScoreReason;
    }

    public void setManualScoreReason(String manualScoreReason) {
        this.manualScoreReason = manualScoreReason;
    }

    public Double scoreIfNullZero() {
        if (!isSuit) {
            return autoScoreValue;
        }
        if (null != manualScore) {
            return manualScore;
        }
        if (null != autoScoreValue) {
            return autoScoreValue;
        }
        return 0.0;
    }

    public Double score() {
        return null != manualScore ? manualScore : autoScoreValue;
    }

    public Double personalScore() {
        return null != autoScoreValue ? autoScoreValue : manualScore;
    }

    public Double getLatestPreHandlerScore() {
        return latestPreHandlerScore;
    }

    public void setLatestPreHandlerScore(Double latestPreHandlerScore) {
        this.latestPreHandlerScore = latestPreHandlerScore;
    }

    public boolean isSuit() {
        return isSuit;
    }

    public void setSuit(boolean suit) {
        isSuit = suit;
    }

    public Object getExtraProp() {
        return extraProp;
    }

    public void setExtraProp(Object extraProp) {
        this.extraProp = extraProp;
    }

    public int getRwState() {
        return rwState;
    }

    public void setRwState(int rwState) {
        this.rwState = rwState;
    }
}
