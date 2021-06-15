package com.example.score.vo;

import com.example.score.constants.ResultCode;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description TODO
 * @Date 2021/4/5 9:59
 * @Created by hdw
 */
public class ResultVO<T> implements Serializable {

    public static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private Integer code = ResultCode.FAIL;

    /**
     * 提示信息
     */
    private String message = "success";

    /**
     * 数据集合
     */
    private T data;

    /**
     * 拓展数据集合
     */
    private Object extData;

    public ResultVO() {
    }

    public ResultVO(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResultVO(T data) {
        this(ResultCode.OK, "OK");
        this.data = data;
    }

    public ResultVO(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ResultVO<T> ok(String message) {
        return ResultVO.ok(message, null);
    }

    public static <T> ResultVO<T> ok(String message, T data) {
        return ResultVO.ok(ResultCode.OK, message, data);
    }

    public static <T> ResultVO<T> ok(Integer code, String message, T data) {
        return new ResultVO<>(code, message, data);
    }

    public static <T> ResultVO<T> fail(String errMessage) {
        return ResultVO.fail(errMessage, null);
    }

    public static <T> ResultVO<T> fail(String errMessage, T data) {
        return ResultVO.fail(ResultCode.FAIL, errMessage, data);
    }

    public static <T> ResultVO<T> fail(Integer code, String errMessage, T data) {
        return new ResultVO<>(code, errMessage, data);
    }

    public static <T> ResultVO<T> warn(String message) {
        return ResultVO.fail(message, null);
    }

    public static <T> ResultVO<T> warn(String message, T data) {
        return ResultVO.fail(ResultCode.WARN, message, data);
    }

    public static <T> ResultVO<T> warn(Integer code, String message, T data) {
        return new ResultVO<>(code, message, data);
    }

    public static <T> ResultVO<T> error(String message, Exception extData) {
        if (ObjectUtils.isEmpty(extData) || extData.getStackTrace().length < 1) {
            return new ResultVO(ResultCode.FAIL, message, null);
        }
        Map<String, Object> returnMap = new HashMap<>(2);
        Map<String, Object> extMap = new HashMap<>(3);
        StackTraceElement element =  extData.getStackTrace()[0];

        extMap.put("fileName", element.getFileName());
        extMap.put("lineNumber",element.getLineNumber());
        extMap.put("methodName",element.getMethodName());
        returnMap.put("data",extData.toString());
        returnMap.put("extData",extData);
        return new ResultVO(ResultCode.FAIL,message,returnMap);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Object getExtData() {
        return extData;
    }

    public void setExtData(Object extData) {
        this.extData = extData;
    }
}
