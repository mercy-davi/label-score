package com.example.score.bean;

/**
 * @Classname ScoreTagConfig
 * @Description TODO
 * @Date 2021/3/31
 * @Author hdw
 */
public class ScoreTagConfig {
    private String scoreCardCode;
    private String ruleDefId;
    private String configKey;
    private String configValue;

    public String getScoreCardCode() {
        return scoreCardCode;
    }

    public void setScoreCardCode(String scoreCardCode) {
        this.scoreCardCode = scoreCardCode;
    }

    public String getRuleDefId() {
        return ruleDefId;
    }

    public void setRuleDefId(String ruleDefId) {
        this.ruleDefId = ruleDefId;
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }
}
