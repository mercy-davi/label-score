package com.example.score.service.impl;

import com.example.score.vo.ResultVO;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Description TODO
 * @Date 2021/4/5 15:44
 * @Created by hdw
 */
@Aspect
@Component
public class ReevaluateAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReevaluateAspect.class);

    @SuppressWarnings("unchecked")
    @AfterReturning(pointcut = "execution(public * com.example.score.service.LabelAndFlowAssistService.add(..))" +
            "|| execution(public * com.example.score.service.LabelAndFlowAssistService.update(..))", returning = "retData")
    public void initReevaluate(JoinPoint joinPoint, Object retData) {
        try {
            // 实际场景需要强转为对应类型
            Object arg = joinPoint.getArgs()[0];
            ResultVO result = (ResultVO) retData;
            // TODO: 2021/5/20 根据参数和返回值进行的实际业务
        } catch (Exception e) {
            LOGGER.error("xxx异常", e);
        }
    }
}
