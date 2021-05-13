package com.example.labelscore.core;

/**
 * @Classname ScoreRuleType
 * @Description TODO
 * @Date 2021/3/31
 * @Author hdw
 */
public enum ScoreRuleType {
    Dict("字典"),
    DictH("字典.5"),
    Interval("区间"),
    IntervalH("区间.5"),
    BooleanExpsMultiVal("字典区间混合"),
    BooleanExpsMultiValH("字典区间混合.5"),
    TwoDimensionMatrix("二维矩阵"),
    TwoDimensionMatrixH("二维矩阵.5"),
    Js("js表达式或函数"),
    SumByWeight("子标签加权求和"),
    ManualInput("手动输入分值");

    private final String name;

    ScoreRuleType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean supportP5() {
        return (this.ordinal() & 1) != 0 || this.ordinal() >= 8;
    }
}