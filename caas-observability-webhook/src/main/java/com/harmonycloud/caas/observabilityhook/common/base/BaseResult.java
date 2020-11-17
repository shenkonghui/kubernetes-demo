package com.harmonycloud.caas.observabilityhook.common.base;

import com.harmonycloud.caas.observabilityhook.common.enums.ErrorCodeMessage;
import lombok.Data;

/**
 * @author dengyulong
 * @date 2020/03/26
 */
@Data
public class BaseResult {

    private static final Integer CODE = 0;

    private Integer code;

    private String msg;

    private Boolean success;

    private Object data;

    private Integer count;

    public BaseResult() {

    }

    public BaseResult(Integer code, boolean success) {
        this.code = code;
        this.success = success;
    }

    public BaseResult(Integer code, String msg, boolean success) {
        this.code = code;
        this.msg = msg;
        this.success = success;
    }

    public BaseResult(Integer code, Object data, boolean success) {
        this.code = code;
        this.data = data;
        this.success = success;
    }

    public BaseResult(Integer code, String msg, Boolean success, Object data, Integer count) {
        this.code = code;
        this.msg = msg;
        this.success = success;
        this.data = data;
        this.count = count;
    }

    public BaseResult(Integer code, String msg, Object data, boolean success) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.success = success;
    }

    public BaseResult(Object data, boolean success) {
        this.data = data;
        this.success = success;
    }

    public static BaseResult error() {
        return error(500, "未知异常，请联系管理员");
    }

    public static BaseResult error(String msg) {
        return error(500, msg);
    }

    public static BaseResult error(ErrorCodeMessage errorCodeMessage) {
        return error(500, errorCodeMessage.getEnMsg());
    }

    public static BaseResult error(ErrorCodeMessage errorCodeMessage, String msg) {
        return error(500, errorCodeMessage.getEnMsg() + ":" + msg);
    }

    public static BaseResult error(Integer code, String msg) {
        return new BaseResult(code, msg, false);
    }

    public static BaseResult ok(String msg) {
        return new BaseResult(CODE, msg, true);
    }

    public static BaseResult ok(Object result) {
        return new BaseResult(CODE, result, true);
    }

    public static BaseResult ok(Object result, Integer count) {
        return new BaseResult(CODE, "success", true, result, count);
    }

    public static BaseResult ok(String msg, Object result) {
        return new BaseResult(CODE, msg, result, true);
    }

    public static BaseResult ok() {
        return new BaseResult(CODE, true);
    }

    public static BaseResult exception(Integer code, Object data) {
        return new BaseResult(code, "", data, false);
    }


}
