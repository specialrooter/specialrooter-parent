package io.specialrooter.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("标准报文返回模型")
public enum ResponseCode {
    SUCCESS("C0000000", "请求成功"),
    FAILURE("C0009999", "其他错误");

    private final String value;
    private final String reasonPhrase;

    ResponseCode(String value, String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }

    public String value() {
        return this.value;
    }

    public String reasonPhrase() {
        return this.reasonPhrase;
    }

    public static ResponseCode parse(String statusCode) {
        ResponseCode[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            ResponseCode status = var1[var3];
            if (status.value == statusCode) {
                return status;
            }
        }

        throw new IllegalArgumentException("No matching constant for [" + statusCode + "]");
    }
}
