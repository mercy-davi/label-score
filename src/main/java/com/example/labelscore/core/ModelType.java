package com.example.labelscore.core;

import com.example.labelscore.entity.Project;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description TODO
 * @Date 2021/4/4 10:33
 * @Created by hdw
 */
public enum ModelType {

    Demo("Project", "样例", Project.class);
//    PE("KypCorporate", "PE", CustInfoCorporate.class);

    ModelType(String code, String name, Class<? extends RootModel> modelClass) {
        this.code = code;
        this.name = name;
        this.modelClass = modelClass;
    }

    private static final Map<String, ModelType> codeMapping;

    static {
        codeMapping = new HashMap<>(ModelType.values().length);
        for (ModelType modelType : ModelType.values()) {
            codeMapping.put(modelType.code, modelType);
        }
    }

    public static ModelType of(String code) {
        ModelType result = codeMapping.get(code);
        if (null == result) {
            throw new IllegalArgumentException("can not find model class of code: " + code);
        }
        return result;
    }

    private final String code;
    private final String name;
    private final Class<? extends RootModel> modelClass;

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public Class<? extends RootModel> getModelClass() {
        return modelClass;
    }

    @Override
    public String toString() {
        return "code='" + code + ", name='" + name;
    }
}
