package com.harmonycloud.caas.observabilityhook.common.exception;

import com.harmonycloud.caas.observabilityhook.common.enums.ErrorCodeMessage;
import com.harmonycloud.caas.observabilityhook.common.base.BaseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author dengyulong
 * @date 2020/03/26
 */
@RestControllerAdvice
public class RestExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * 运行时异常
     *
     * @param e 异常
     * @return
     */
    @ExceptionHandler(CaasRuntimeException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseResult nullPointerExceptionHandler(Exception e) {
        CaasRuntimeException exception = (CaasRuntimeException) e;
        String errorMessage = exception.getErrorMessage() == null ? (e.getMessage()) : exception.getErrorMessage();
        logger.warn(errorMessage, e);
        return BaseResult.error(exception.getErrorCode(), errorMessage);
    }

    /**
     * 所有的exception异常
     *
     * @param e 异常
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseResult exceptionHandler(Exception e) {
        logger.error(e.getMessage(), e);
        return BaseResult.error(ErrorCodeMessage.UNKNOWN);
    }

}
