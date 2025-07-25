package com.huiminpay.merchant.common.intercept;

import com.huiminpay.common.cache.domain.BusinessException;
import com.huiminpay.common.cache.domain.CommonErrorCode;
import com.huiminpay.common.cache.domain.ErrorCode;
import com.huiminpay.common.cache.domain.RestErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    //处理全局异常
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse handleException(HttpServletRequest request, HttpServletResponse response, Exception e) {

        //根据不同异常类型，返回不同的错误码和错误信息
        if (e instanceof BusinessException){
            // 可预知的异常，取错误代码错误信息，组装对象返回
            BusinessException businessException = (BusinessException) e;
            ErrorCode errorCode = businessException.getErrorCode();
            // 错误代码
            int code = errorCode.getCode();
            // 错误信息
            String desc = errorCode.getDesc();
            // 组装对象返回
            return new RestErrorResponse(String.valueOf(code), desc);
        }
        // 未知异常，返回系统错误
        return new RestErrorResponse(String.valueOf(CommonErrorCode.UNKNOWN.getCode()),
                CommonErrorCode.UNKNOWN.getDesc());
    }

}
