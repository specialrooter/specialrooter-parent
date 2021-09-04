package io.specialrooter.context.annotation;

public enum EnumViewOption {
    ENUM("ENUM","枚举值"),
    KEY("KEY","实际值"),
    TEXT("TEXT","显示值");

    private final String code;
    private final String text;

    EnumViewOption(String code, String text) {
        this.code = code;
        this.text = text;
    }

    public String getCode() {
        return code;
    }

    public String getText() {
        return text;
    }
}
