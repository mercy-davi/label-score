package com.example.labelscore.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @Description 此类为处理系统中其他配置文件的属性值获取的统一入口
 * 使用方式，先把配置属性加进来，然后定义私有静态属性，再定义私有set方法并注入，再定义公有静态方法提供对外访问
 * 提供 systemCompanyCode  Demo 如下
 * @Date 2021/4/5 10:00
 * @Created by hdw
 */
@PropertySource("classpath:label-score-application.properties")
@Component
public class ConfigUtil {

    /**
     * 公司编码
     */
    private static String systemCompanyCode;

    @Value("${System_Company_Code}")
    private void setSystemCompanyCode(String systemCompanyCode) {
        ConfigUtil.systemCompanyCode = systemCompanyCode;
    }

    public static String getSystemCompanyCode() {
        return systemCompanyCode;
    }
}
