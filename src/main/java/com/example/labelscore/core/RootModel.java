package com.example.labelscore.core;

import com.example.labelscore.bean.DictInfo;

import java.io.Serializable;
import java.util.Map;

/**
 * @Description TODO
 * @Date 2021/4/4 10:55
 * @Created by hdw
 */
public abstract class RootModel implements Serializable {
    public Object _id;
    String summary;

    protected transient String _ModelCode;
    protected transient Map<String, Map<String, DictInfo>> dictInfoMap;

    protected RootModel(String modelCode) {
        this._ModelCode = modelCode;
    }

    public String getModelCode() {
        return _ModelCode;
    }

    public String summary() {
        if (null == summary) {
            summary = "id: " + _id + ", modelCode: " + _ModelCode;
        }
        return summary;
    }

    public Map<String, Map<String, DictInfo>> getDictInfoMap() {
        return dictInfoMap;
    }

    public void setDictInfoMap(Map<String, Map<String, DictInfo>> dictInfoMap) {
        this.dictInfoMap = dictInfoMap;
    }
}
