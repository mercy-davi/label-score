package com.example.score.util;

import com.example.score.service.ScoreTagConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

import static com.example.score.service.ScoreTagConfigService.AFTER_NEED_UPDATE_RULE_CODE;

/**
 * @Classname ScoreTagConfigUtil
 * @Description TODO
 * @Date 2021/4/9
 * @Author hdw
 */
@Component
public class ScoreTagConfigUtil {

    @Autowired
    private ScoreTagConfigService scoreTagConfigService;

    private static ScoreTagConfigService scoreTagConfigServiceImpl;

    @PostConstruct
    public void init() {
        ScoreTagConfigUtil.scoreTagConfigServiceImpl = scoreTagConfigService;
    }

    public static Map<String, String[]>[] findAfterNeedUpdateRuleCodeConfig(String scoreCardCode) {
        return scoreTagConfigServiceImpl.findAfterNeedUpdateRuleCodeConfig(new String[]{AFTER_NEED_UPDATE_RULE_CODE}, scoreCardCode);
    }
}
