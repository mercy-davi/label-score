package com.example.score.core;

import com.example.score.bean.TagRuleInstance;
import com.example.score.constants.LabelConstants;
import com.example.score.util.JsonUtil;
import com.example.score.util.ScoreTagConfigUtil;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import javax.script.ScriptException;
import java.util.Map;

import static com.example.score.constants.LabelConstants.RULE_CODE;
import static com.example.score.core.ScoreRuleType.Interval;
import static com.example.score.core.ScoreRuleType.IntervalH;

/**
 * @Classname AutoJsValueSourceExp
 * @Description TODO
 * @Date 2021/3/31
 * @Author hdw
 */
public class AutoJsValueSourceExp extends ValueSourceExp {
    private static final Logger LOGGER = LoggerFactory.getLogger(AutoJsValueSourceExp.class);

    public AutoJsValueSourceExp() {
        this.valueSourceType = VALUE_SOURCE_TYPE_AUTO_JS;
    }

    @Override
    public boolean isAuto() {
        return true;
    }

    @Override
    public Object value(RuleContext context, TagRuleInstance tagRuleInstance, String valueSourceStr) {
        try {
            TagRuleInstance parent = tagRuleInstance.parent().parent().parent();
            context.putObjInContextBinding("parent", parent);
            Object value = JSEngine.get().eval(this.getExp(), context.getBindings());
            if (LabelConstants.AFTER_LABEL_SCORE.equals(context.getObjInContextBinding("after"))
                    && !StringUtils.isEmpty(valueSourceStr)) {
                Map<String, String[]>[] config = ScoreTagConfigUtil.findAfterNeedUpdateRuleCodeConfig(parent.getDefinition().getRuleCode());
                String[] ruleCodes = config[0].get(RULE_CODE);
                if (!ObjectUtils.isEmpty(value) && ArrayUtils.contains(ruleCodes, tagRuleInstance.getDefinition().getRuleCode())) {
                    // 319投后打分当已有值且能取到值时使用最新值，AutoJsDictValueSourceExp同步改造
                    return value;
                }
                ScoreRuleType scoreType = tagRuleInstance.getDefinition().getScoreType();
                if (ObjectUtils.isEmpty(value) && (Interval.equals(scoreType) || IntervalH.equals(scoreType))) {
                    return formatValueSource(valueSourceStr, Double.class);
                }
                if ((value instanceof Number)) {
                    return formatValueSource(valueSourceStr, value.getClass());
                } else {
                    return valueSourceStr;
                }
            }
            return value;
        } catch (ScriptException ex) {
            LOGGER.info("AutoJsValueSourceExp eval error, maybe value exp definition error, scoreRuleDefId:" +
                    tagRuleInstance.getScoreRuleDefId() +
                    (!context.hasModel() ? "" : ", rootModel:[" + context.getRoot().summary() + "]"));
            return EXCEPTION_FALLBACK_VALUE;
        } catch (IndexOutOfBoundsException ex) {
            LOGGER.info("maybe data list is empty or size is less than expected");
            return null;
        }
    }

    @Override
    public String getExp() {
        if (null == exp) {
            return null;
        }
        if (exp instanceof String) {
            return (String) exp;
        }
        throw new RuleExprInvalidException("AutoJsValueSourceExp exp must be a string");
    }

    private <T> T formatValueSource(String valueSource, Class<T> clazz) {
        DataType dataType = valueSource.contains(".") ? DataType.PERCENT : DataType.NUMBER;
        Object convert = dataType.converter().convert(valueSource);

        return ObjectUtils.isEmpty(convert) ? null : JsonUtil.jsonToBean(convert.toString(), clazz);
    }
}
