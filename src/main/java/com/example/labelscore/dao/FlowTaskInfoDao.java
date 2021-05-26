package com.example.labelscore.dao;

import com.example.labelscore.bean.FlowTaskInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Classname FlowTaskInfoDao
 * @Date 2021/5/20
 * @Author hdw
 */
@Mapper
public interface FlowTaskInfoDao {
    String[] findAllPossibleTaskIds(FlowTaskInfo flowTaskInfo);

    String[] findLatestBackOrBeforeTaskIds(FlowTaskInfo flowTaskInfo);

    String[] findPossibleLatestBack(FlowTaskInfo flowTaskInfo);

    String[] findSpecialParallelBeforeNode(@Param("flowTaskInfo") FlowTaskInfo flowTaskInfo,
                                           @Param("phaseNos") String[] phaseNos);

    String[] findDecisionCommitteeTaskIds(@Param("objectNo") String objectNo,
                                          @Param("objectType") String objectType,
                                          @Param("phaseNos") String[] phaseNos);
}
