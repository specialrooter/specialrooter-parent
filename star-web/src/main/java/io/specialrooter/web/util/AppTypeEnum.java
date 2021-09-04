package io.specialrooter.web.util;

/**
 * 平台类型
 */
public enum AppTypeEnum {
    RESELLER(1, "分销商"),
    AGENT(2, "代理商"),
    SUPPLIER(3, "供应商"),
    OPERATOR(4, "运营后台"),
    OPEN(5, "开放平台"),
    OPERATION(6, "运营平台");

    private final Integer code;

    private final String message;

    AppTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return message;
    }
}