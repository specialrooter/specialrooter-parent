package io.specialrooter.holiday;

import java.time.LocalDate;

/**
 * 节假日
 */
public class Holiday {
    // 节假日名称
    private String name;
    // 日期
    private LocalDate date;
    // true表示是节假日，false表示是调休
    private boolean holiday;
    // 只在调休下有该字段。true表示放完假后调休，false表示先调休再放假
    private boolean after;
    // 表示当前时间距离目标还有多少天
    private Integer rest;
    // 薪资倍数
    private Integer wage;
    // 0工作日、1周末、2节日、3调休
    @Deprecated
    private String type;

    public Holiday() {
    }

    public Holiday(String name, LocalDate date, boolean holiday) {
        this.name = name;
        this.date = date;
        this.holiday = holiday;
    }

    public Holiday(String name, LocalDate date, boolean holiday, boolean after, Integer rest) {
        this.name = name;
        this.date = date;
        this.holiday = holiday;
        this.after = after;
        this.rest = rest;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public boolean isHoliday() {
        return holiday;
    }

    public void setHoliday(boolean holiday) {
        this.holiday = holiday;
    }

    public boolean isAfter() {
        return after;
    }

    public void setAfter(boolean after) {
        this.after = after;
    }

    public Integer getRest() {
        return rest;
    }

    public void setRest(Integer rest) {
        this.rest = rest;
    }

    public Integer getWage() {
        return wage;
    }

    public void setWage(Integer wage) {
        this.wage = wage;
    }
}
