package com.example.score.bean;

import com.example.score.core.ScoreRuleType;
import com.example.score.core.ValueSourceExp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Description TODO
 * @Date 2021/4/3 10:04
 * @Created by hdw
 */
@JsonIgnoreProperties({"ruleContent", "parentRuleDef", "children"})
public class TagRuleDef implements HierarchyRecord<String>, Serializable {
    private static final long serialVersionUID = 1L;

    // primary key
    private String id;
    private String ruleCode;
    private String ruleDescription;
    private Double ruleWeight;
    private String ruleParentId;

    private String scoreExp;
    private ScoreRuleType scoreType;
    private Integer version;
    private int level;
    private String sortCode;
    private String displayName;
    private ValueSourceExp valueSourceExp;
    private boolean isHistory;
    private Double defaultScore;
    private String valueDescription;

    private transient TagRuleDef parentRuleDef;

    @JsonView(TagView.Def.class)
    private transient List<TagRuleDef> children;


    @Override
    public int level() {
        return level;
    }

    @Override
    public String code() {
        return ruleCode;
    }

    @Override
    public String getParentCode() {
        return null != parentRuleDef ? parentRuleDef.getRuleCode() : ruleParentId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }

    public String getRuleDescription() {
        return ruleDescription;
    }

    public void setRuleDescription(String ruleDescription) {
        this.ruleDescription = ruleDescription;
    }

    public Double getRuleWeight() {
        return ruleWeight;
    }

    public void setRuleWeight(Double ruleWeight) {
        this.ruleWeight = ruleWeight;
    }

    public String getRuleParentId() {
        return getParentCode();
    }

    public void setRuleParentId(String ruleParentId) {
        this.ruleParentId = ruleParentId;
    }

    public String getScoreExp() {
        return scoreExp;
    }

    public void setScoreExp(String scoreExp) {
        this.scoreExp = scoreExp;
    }

    public ScoreRuleType getScoreType() {
        return scoreType;
    }

    public void setScoreType(ScoreRuleType scoreType) {
        this.scoreType = scoreType;
    }

    public boolean isHistory() {
        return isHistory;
    }

    public void setHistory(boolean history) {
        isHistory = history;
    }

    @Override
    public void parent(HierarchyRecord<String> parent) {
        this.parentRuleDef = (TagRuleDef) parent;
        this.parentRuleDef.addChildNoCheckDuplicate(this);
    }

    @Override
    public TagRuleDef parent() {
        return this.parentRuleDef;
    }

    public String[] childrenCodes() {
        if (!hasChild()) {
            return new String[0];
        }
        return children.stream().map(def -> def.ruleCode).toArray(String[]::new);
    }

    public String[] siblingCodes() {
        return parent().childrenCodes();
    }

    @Override
    public List<TagRuleDef> getChildren() {
        return children;
    }

    public void setChildren(List<TagRuleDef> children) {
        this.children = children;
    }

    @Override
    public void justSetParent(HierarchyRecord<String> parent) {
        this.parentRuleDef = (TagRuleDef) parent;
    }

    private void addChildNoCheckDuplicate(TagRuleDef tagRuleDef) {
        if (null == children) {
            children = new ArrayList<>(DEFAULT_CHILDREN_SIZE);
        }
        children.add(tagRuleDef);
    }

    @Override
    public boolean hasChild() {
        return !CollectionUtils.isEmpty(children);
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getSortCode() {
        return sortCode;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public ValueSourceExp getValueSourceExp() {
        return valueSourceExp;
    }

    public boolean isValueAuto() {
        return null != valueSourceExp && valueSourceExp.isAuto();
    }

    public void setValueSourceExp(ValueSourceExp valueSourceExp) {
        this.valueSourceExp = valueSourceExp;
    }

    public boolean isNoChange(TagRuleDef other) {
        return Objects.equals(this.ruleWeight, other.ruleWeight)
                && Objects.equals(this.scoreExp, other.scoreExp)
                && Objects.equals(this.scoreType, other.scoreType)
                && Objects.equals(this.valueSourceExp, other.valueSourceExp)
                && ((null == this.parentRuleDef && null == other.parentRuleDef)
                || (null != this.parentRuleDef && null != other.parentRuleDef
                && Objects.equals(this.parentRuleDef.ruleCode, other.parentRuleDef.ruleCode)));
    }

    public boolean isScoreRuleNoChange(TagRuleDef other) {
        return Objects.equals(this.scoreType, other.scoreType)
                && Objects.equals(this.scoreExp, other.scoreExp);
    }

    public Double getDefaultScore() {
        return defaultScore;
    }

    public void setDefaultScore(Double defaultScore) {
        this.defaultScore = defaultScore;
    }

    public String getValueDescription() {
        return valueDescription;
    }

    public void setValueDescription(String valueDescription) {
        this.valueDescription = valueDescription;
    }
}
