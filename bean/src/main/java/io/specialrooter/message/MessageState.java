package io.specialrooter.message;

import io.swagger.annotations.ApiModel;

/**
 * Created by Ai on 2017/5/10.
 */
@ApiModel("请求返回状态码")
public enum MessageState {
    SUCCESS(200, "成功"),
    FAILURE(102, "失败"),
    USER_NEED_AUTHORITIES(201, "用户未登录"),
    USER_LOGIN_FAILED(202, "用户账号或密码错误"),
    USER_LOGIN_SUCCESS(203, "用户登录成功"),
    USER_NO_ACCESS(204, "用户无权访问"),
    USER_LOGOUT_SUCCESS(205, "用户登出成功"),
    TOKEN_IS_BLACKLIST(206, "此token已失效"),
    TOKEN_IS_UNLAWFUL(207, "此token已失效,请重新登录"),
    LOGIN_IS_OVERDUE(208, "登录已失效"),
    SERVER_LOGIC_ERROR(500,"服务端逻辑错误"),
    FORECASTING_ERROR(513, "可预判的错误!"),
    UNDEFINED_ERROR(500000, "未定义错误编码!"),
    FORECASTING_UNIQUE_ERROR(514, "属性唯一错误!");

    private final int value;
    private final String reasonPhrase;

    MessageState(int value, String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }

    public int value() {
        return this.value;
    }

    public String reasonPhrase() {
        return this.reasonPhrase;
    }

    public static MessageState parse(int statusCode) {
        MessageState[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            MessageState status = var1[var3];
            if (status.value == statusCode) {
                return status;
            }
        }

        throw new IllegalArgumentException("No matching constant for [" + statusCode + "]");
    }
}
