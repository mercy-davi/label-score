package com.example.labelscore.bean;

/**
 * @Description TODO
 * @Date 2021/4/3 10:56
 * @Created by hdw
 */
public class TagRuleInstanceWithDef extends TagRuleInstance {
    private String defId;
    private String defRuleCode;
    private String defRuleDescription;
    private String defRuleParentId;
    private String defRuleWeight;
    private String defRuleContent;
    private String defRuleParam;
    private String defRuleType;
    private String defVersion;

    public String getDefId() {
        return defId;
    }

    public void setDefId(String defId) {
        this.defId = defId;
    }

    public String getDefRuleCode() {
        return defRuleCode;
    }

    public void setDefRuleCode(String defRuleCode) {
        this.defRuleCode = defRuleCode;
    }

    public String getDefRuleDescription() {
        return defRuleDescription;
    }

    public void setDefRuleDescription(String defRuleDescription) {
        this.defRuleDescription = defRuleDescription;
    }

    public String getDefRuleParentId() {
        return defRuleParentId;
    }

    public void setDefRuleParentId(String defRuleParentId) {
        this.defRuleParentId = defRuleParentId;
    }

    public String getDefRuleWeight() {
        return defRuleWeight;
    }

    public void setDefRuleWeight(String defRuleWeight) {
        this.defRuleWeight = defRuleWeight;
    }

    public String getDefRuleContent() {
        return defRuleContent;
    }

    public void setDefRuleContent(String defRuleContent) {
        this.defRuleContent = defRuleContent;
    }

    public String getDefRuleParam() {
        return defRuleParam;
    }

    public void setDefRuleParam(String defRuleParam) {
        this.defRuleParam = defRuleParam;
    }

    public String getDefRuleType() {
        return defRuleType;
    }

    public void setDefRuleType(String defRuleType) {
        this.defRuleType = defRuleType;
    }

    public String getDefVersion() {
        return defVersion;
    }

    public void setDefVersion(String defVersion) {
        this.defVersion = defVersion;
    }
}
