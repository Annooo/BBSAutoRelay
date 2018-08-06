package com.cn.BBSAutoRelay.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * @deprecated: 统一异常处理
 * @date 2018年8月2日 16点56分
 * @version 1.0
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    protected Logger logger =  LoggerFactory.getLogger(this.getClass());

    public static final String DEFAULT_ERROR_VIEW = "error";

    @ExceptionHandler(value = Exception.class)
    public ModelAndView defaultErrorHandler(Exception e, HttpServletRequest request) throws Exception {
        logger.info("请求地址: {}",request.getRequestURL());
        ModelAndView model = new ModelAndView();
        logger.error("异常信息: {}",e);
        model.setViewName(DEFAULT_ERROR_VIEW);
        return model;
    }
}
