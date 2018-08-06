package com.cn.BBSAutoRelay.annotation;

import java.lang.annotation.*;

/**
 * @Description: 日志注解
 * @date 2018年8月2日 16点25分
 * @version 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LoggerManage {

    public String description();

}
