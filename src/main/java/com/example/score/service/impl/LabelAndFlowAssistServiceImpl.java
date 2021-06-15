package com.example.score.service.impl;

import com.example.score.bean.FlowTaskInfo;
import com.example.score.bean.TagRuleInstance;
import com.example.score.cache.ScoreLocalCache;
import com.example.score.constants.LabelConstants;
import com.example.score.dao.FlowTaskInfoDao;
import com.example.score.service.ContextStrMaker;
import com.example.score.service.LabelAndFlowAssistService;
import com.example.score.service.ScoreTagConfigService;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.example.score.service.ScoreTagConfigService.CONFIG_KEY_SCORE_PHASE_NO;
import static com.example.score.service.ScoreTreeLevel.PRIMARY_TAG;

/**
 * @Description TODO
 * @Date 2021/4/5 15:41
 * @Created by hdw
 */
@Service
public class LabelAndFlowAssistServiceImpl implements LabelAndFlowAssistService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LabelAndFlowAssistServiceImpl.class);

    @Autowired
    ContextStrMaker contextStrMaker;

    @Autowired
    private FlowTaskInfoDao flowTaskInfoDao;

    @Autowired
    private ScoreLocalCache scoreLocalCache;

    @Autowired
    private ScoreTagConfigService scoreTagConfigService;

    @Override
    public void add() {

    }

    @Override
    public void update() {

    }

    @Override
    public String[] makeContexts(String prefix, String[] ids) {
        int length = ids.length;
        String[] results = new String[length];
        for (int i = 0; i < length; i++) {
            results[i] = contextStrMaker.make(prefix, ids[i]);
        }
        return results;
    }

    @Override
    public String[] findAllPossibleTaskIds(FlowTaskInfo flowTaskInfo) {
        String[] taskIds = flowTaskInfoDao.findAllPossibleTaskIds(flowTaskInfo);
        if (taskIds.length == 0 || !ArrayUtils.contains(taskIds, flowTaskInfo.getId())) {
            LOGGER.info("There may exist some mistakes in flow service, and should make begin or end time more clear. " +
                    "To handle this situation, extra logic will be performed as following.");
            String[] newTaskIds = Arrays.copyOf(taskIds, taskIds.length + 1);
            newTaskIds[taskIds.length] = flowTaskInfo.getId();
            taskIds = newTaskIds;
        }
        return taskIds;
    }

    @Override
    public String[] findLatestBackOrBeforeTaskIds(FlowTaskInfo flowTaskInfo) {
        return flowTaskInfoDao.findLatestBackOrBeforeTaskIds(flowTaskInfo);
    }

    @Override
    public String[] getScorePhaseNo(String typeName) {
        if (StringUtils.isEmpty(typeName)) {
            return new String[0];
        }
        Map<String, String[]>[] scorePhaseNoConfig = scoreLocalCache.get(CONFIG_KEY_SCORE_PHASE_NO);
        if (ObjectUtils.isEmpty(scorePhaseNoConfig) || ArrayUtils.isEmpty(scorePhaseNoConfig[0].get(typeName))) {
            scorePhaseNoConfig  = scoreTagConfigService.findScorePhaseNoConfig(new String[]{CONFIG_KEY_SCORE_PHASE_NO}, null);
            scoreLocalCache.put(CONFIG_KEY_SCORE_PHASE_NO, scorePhaseNoConfig);
        }
        return scorePhaseNoConfig[0].get(typeName);
    }

    @Override
    public void handleTopTagRuleInstance(List<TagRuleInstance> children, String scoreType) {
        for (TagRuleInstance instance : children) {
            if (!instance.isSuit()) {
                if (LabelConstants.SCORE_TYPE_SETUP.equals(scoreType)
                        || LabelConstants.SCORE_TYPE_REEVALUATE.equals(scoreType)) {
                    instance.setAutoScoreValue(instance.getLevel() == PRIMARY_TAG ? null : 1.0);
                } else {
                    instance.setManualScore(null);
                }
                if (!ObjectUtils.isEmpty(instance.getChildren())) {
                    handleTopTagRuleInstance(instance.getChildren(), scoreType);
                }
            }
        }
    }
}
