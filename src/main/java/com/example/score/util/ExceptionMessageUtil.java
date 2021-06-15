package com.example.score.util;

import com.example.score.vo.ResultVO;

import java.util.HashMap;
import java.util.Map;

/**
 * @Classname ExceptionMessageUtil
 * @Description TODO
 * @Date 2021/4/9
 * @Author hdw
 */
public class ExceptionMessageUtil {

    /**
     * 返回异常信息String
     * @param e 异常
     * @return String
     */
    public static String errorMessage(Exception e) {
        StackTraceElement element = e.getStackTrace()[0];
        return  "异常类: "
                + element.getFileName()
                + ", 第"
                + element.getLineNumber()
                + "行, 异常信息: " + e.getMessage()
                + ", 异常类型: " + e.getClass().getName();
    }

    /**
     *
     * @param e 异常
     * @param message 异常返回的中文信息描述
     * @return ResultVO
     */
    public static ResultVO errResultVO(Exception e, String message) {
        StackTraceElement element = e.getStackTrace()[0];
        Map<String, Object> extData = new HashMap<String, Object>() {
            {
                put("fileName", element.getFileName());
                put("lineNumber", element.getLineNumber());
            }
        };
        ResultVO resultVO = ResultVO.fail(message);
        resultVO.setData(e.toString());
        resultVO.setExtData(extData);

        return resultVO;
    }
}
