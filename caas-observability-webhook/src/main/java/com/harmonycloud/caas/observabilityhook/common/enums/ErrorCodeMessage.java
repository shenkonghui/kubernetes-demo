package com.harmonycloud.caas.observabilityhook.common.enums;

/**
 * @author dengyulong
 * @date 2020/03/26
 */
public enum ErrorCodeMessage {

    /**
     * 错误枚举类
     */
    OK(200, "success", "操作成功"),

    // 通用错误 1xxxxx
    UNKNOWN(100001, "System error, please contact Administrator.", "系统错误，请联系系统管理员"),
    INVALID_PARAMETER(100002, "Invalid parameter.", "参数错误"),
    INVALID_CONFIG(100003, "Invalid Config.", "配置错误"),
    PARAMETER_VALUE_NOT_PROVIDE(100004, "Parameter cannot be null.", "参数不能为空"),
    NOT_BLANK(100005, "Can not be blank.", "不能为空"),
    RUN_COMMAND_ERROR(100007, "Command executed failed.","命令执行失败"),
    OPERATION_FAIL(100008, "Operation failed.","操作失败"),


    // 分区 302xxx
    NAMESPACE_NOT_FOUND(302002, "Namespace not found.","分区未找到"),

    ;


    private final int code;
    private final String enMsg;
    private final String cnMsg;

    ErrorCodeMessage(int code, String enMsg, String cnMsg) {
        this.code = code;
        this.enMsg = enMsg;
        this.cnMsg = cnMsg;
    }

    public String getMsg() {
        // 默认返回英文
        return enMsg;
    }

    public static ErrorCodeMessage valueOf(int statusCode) {
        ErrorCodeMessage[] var1 = values();

        for (ErrorCodeMessage status : var1) {
            if (status.code == statusCode) {
                return status;
            }
        }

        throw new IllegalArgumentException("No matching constant for [" + statusCode + "]");
    }

    public int getCode() {
        return code;
    }

    public String getEnMsg() {
        return enMsg;
    }

    public String getCnMsg() {
        return cnMsg;
    }

}
