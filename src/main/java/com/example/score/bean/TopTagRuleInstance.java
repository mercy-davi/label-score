package com.example.score.bean;

import java.io.Serializable;

/**
 * @Description TODO
 * @Date 2021/4/3 11:02
 * @Created by hdw
 */
public class TopTagRuleInstance extends TagRuleInstance implements Serializable {
    private static final long serialVersionUID = 1L;

    private transient ScoreBizSource scoreBizSource;
    private int itemCount;
    private int maxDepth;
    private boolean isReadOnly;
    private boolean isEmpty = false;

    public ScoreBizSource getScoreBizSource() {
        return scoreBizSource;
    }

    public TopTagRuleInstance setScoreBizSource(ScoreBizSource scoreBizSource) {
        this.scoreBizSource = scoreBizSource;
        return this;
    }

    public int getItemCount() {
        return itemCount;
    }

    public TopTagRuleInstance setItemCount(int itemCount) {
        this.itemCount = itemCount;
        return this;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public TopTagRuleInstance setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
        return this;
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }

    public TopTagRuleInstance setReadOnly(boolean readOnly) {
        isReadOnly = readOnly;
        return this;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public TopTagRuleInstance setEmpty(boolean empty) {
        isEmpty = empty;
        return this;
    }

    public static TopTagRuleInstance from(TagRuleInstance ruleInstance) {
        TopTagRuleInstance top = new TopTagRuleInstance();
        top.setId(ruleInstance.getId());
        top.setScoreRuleDefId(ruleInstance.getScoreRuleDefId());
        top.setAutoScoreValue(ruleInstance.getAutoScoreValue());
        // top.setParentInstanceId(ruleInstance.getParentInstanceId());
        top.setValueSource(ruleInstance.getValueSource());
        top.setManualScore(ruleInstance.getManualScore());
        top.setManualScoreReason(ruleInstance.getManualScoreReason());
        // top.parent(ruleInstance.parent());
        top.setChildren(ruleInstance.getChildren());
        top.setDefinition(ruleInstance.getDefinition());
        top.setLevel(ruleInstance.getLevel());
        top.setRwState(ruleInstance.getRwState());
        // top.setExtraProp(ruleInstance.getExtraProp());
        return top;
    }
}
