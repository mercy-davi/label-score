package com.example.labelscore.constants;

/**
 * @Classname ResultCode
 * @Date 2021/5/20
 * @Author hdw
 */
public interface ResultCode {

    /**
     * 请求成功
     */
    int OK = 200;

    /**
     * 权限不足
     */
    int NO_AUTHORITY = 1001;

    /**
     * 参数校验有误
     */
    int PARAM_ERROR = 1002;

    /**
     * 业务异常
     */
    int BIZ_EXCEPTION = 2000;

    /**
     * 无法识别异常
     */
    int UNKNOWN_EXCEPTION = 9999;

    /**
     * 通用错误编码
     */
    int FAIL = -1;

    /**
     * 警告
     */
    int WARN = -2;
}
