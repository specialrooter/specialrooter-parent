package io.specialrooter.context.annotation;

public enum DateFill {
    START_TIME(" 00:00:00"), END_TIME(" 23:59:59"),TO_DAY(" 88:88:88");
    private String fill;

    DateFill(String fill) {
        this.fill = fill;
    }

    public String value() {
        return this.fill;
    }
}
