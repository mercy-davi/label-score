package com.example.score.bean;

import java.io.Serializable;

/**
 * @Classname DictInfo
 * @Date 2021/5/20
 * @Author hdw
 */
public class DictInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String dictCode;
    private String dictName;

    public String getDictCode() {
        return dictCode;
    }

    public void setDictCode(String dictCode) {
        this.dictCode = dictCode;
    }

    public String getDictName() {
        return dictName;
    }

    public void setDictName(String dictName) {
        this.dictName = dictName;
    }
}
