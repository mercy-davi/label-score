package com.example.labelscore.core;

import javax.script.Bindings;

/**
 * @Description TODO
 * @Date 2021/4/4 14:53
 * @Created by hdw
 */
public class RuleContext {
    public static final String ROOT_MODEL = "rootModel";
    public static final String TAG_VALUE = "tagValue";

    private final RootModel root;
    private Bindings bindings;

    public RuleContext(RootModel root) {
        this.root = root;
    }

    private void ensureBindingInit() {
        if (null == bindings) {
            bindings = JSEngine.get().createBindings();
            if (null != root) {
                bindings.put(ROOT_MODEL, root);
            }
        }
    }

    public RootModel getRoot() {
        return root;
    }

    public Bindings getBindings() {
        ensureBindingInit();
        return bindings;
    }

    @SuppressWarnings("unchecked")
    public <T> T getObjInContextBinding(String key) {
        if (null == bindings) {
            return null;
        }
        return (T) bindings.get(key);
    }

    public void putObjInContextBinding(String key, Object value) {
        getBindings().put(key, value);
    }

    public boolean containsObjInContextBinding(String key) {
        return null != bindings && bindings.containsKey(key);
    }

    public boolean hasModel() {
        return null != root;
    }
}
