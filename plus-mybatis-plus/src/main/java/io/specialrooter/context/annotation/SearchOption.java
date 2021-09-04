package io.specialrooter.context.annotation;

public enum SearchOption {
    EQ("EQ","等于"),
    CP("CP","包含"),
    GT("GT","大于"),
    GE("GE","大于等于"),
    NE("NE","不等于"),
    LT("LT","小于"),
    LE("LE","小于等于"),
    BT("BT","区间"),
    BTP("BTP","特殊区间"),
    IN("IN","多值"),
    NI("NI","NOT IN"),
    AUTO("AU","自动");

    private final String code;
    private final String text;

    SearchOption(String code, String text) {
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
