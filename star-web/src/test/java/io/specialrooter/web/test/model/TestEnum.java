package io.specialrooter.web.test.model;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum TestEnum {
    STYLE_CODE_1(1, "轮播图"),
    STYLE_CODE_2(2, "一行一列"),
    STYLE_CODE_3(3, "一行三列"),
    STYLE_CODE_4(4, "一行五列"),
    STYLE_CODE_5(5, "豆腐块"),
    STYLE_CODE_6(6, "信息样式");

    @EnumValue
    private final int code;
    private final String msg;

    TestEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return msg;
    }
}
