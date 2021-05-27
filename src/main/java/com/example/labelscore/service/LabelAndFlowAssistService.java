package com.example.labelscore.service;

import com.example.labelscore.bean.FlowTaskInfo;
import com.example.labelscore.bean.TagRuleInstance;

import java.util.List;

/**
 * @Description TODO
 * @Date 2021/4/4 22:15
 * @Created by hdw
 */
public interface LabelAndFlowAssistService {

    void add();

    void update();

    String[] makeContexts(String prefix, String[] ids);

    String[] findAllPossibleTaskIds(FlowTaskInfo flowTaskInfo);

    String[] findLatestBackOrBeforeTaskIds(FlowTaskInfo flowTaskInfo);

    String[] getScorePhaseNo(String typeName);

    void handleTopTagRuleInstance(List<TagRuleInstance> children, String scoreType);
}
