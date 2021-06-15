package com.example.score.core;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import static javax.script.ScriptContext.GLOBAL_SCOPE;

/**
 * @Description TODO
 * @Date 2021/4/3 22:57
 * @Created by hdw
 */
public class JSEngine {
    static final String _2FIX_NUMBER_SCRIPT = "function _2FN(val, fix){ if (typeof val == 'string') " +
            "{ val = val.replace(/[^\\d\\.]/g, '') } val = parseFloat(val); if (isNaN(val)) return null; if (null == fix) {return val; } return parseFloat(val.toFixed(fix)) }";
    private static ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");

    static {
        try {
            Object obj = scriptEngine.eval(_2FIX_NUMBER_SCRIPT);
            scriptEngine.getBindings(GLOBAL_SCOPE).put("_2FN", obj);
        } catch (ScriptException ex) {
            throw new IllegalStateException("init script engine error", ex);
        }
    }

    public static ScriptEngine get() {
        return scriptEngine;
    }
}
