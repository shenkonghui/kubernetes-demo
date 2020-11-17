package com.harmonycloud.caas.observabilityhook.common.exception;

import com.harmonycloud.caas.observabilityhook.common.enums.ErrorCodeMessage;

/**
 * @author dengyulong
 * @date 2020/03/26
 */
public class CaasRuntimeException extends RuntimeException {

    private Integer errorCode;
    private String errorName;
    private String errorMessage;

    public CaasRuntimeException() {
    }

    public CaasRuntimeException(String message) {
        super(message);
    }

    public CaasRuntimeException(String message, Integer errorCode) {
        super(message);
        this.errorMessage = message;
        this.errorCode = errorCode;
    }

    public CaasRuntimeException(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public CaasRuntimeException(Throwable cause) {
        super(cause);
    }

    public CaasRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public CaasRuntimeException(ErrorCodeMessage error) {
        super(error.getMsg());
        this.errorCode = error.getCode();
        this.errorName = error.name();
        this.errorMessage = error.getMsg();
    }

    public CaasRuntimeException(ErrorCodeMessage error, String msg) {
        super(error.getMsg() + ":" + msg);
        this.errorCode = error.getCode();
        this.errorName = error.name();
        this.errorMessage = error.getMsg() + ":" + msg;
    }

    public Integer getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public String getErrorName() {
        return this.errorName;
    }

    public void setErrorName(String errorName) {
        this.errorName = errorName;
    }
    
}
