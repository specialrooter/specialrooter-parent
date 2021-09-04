package io.specialrooter.plus.mybatisplus.util;

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

public class DateUtils {
    public static final String DATE_T_TIME_Z = "([0-9]{4})-([0-1][0-9])-([0-3][0-9])T([0-2][0-9]):([0-6][0-9]):([0-6][0-9]).([0-9][0-9][0-9])Z";
    public static final String DATE_T_TIME_K = "([0-9]{4})-([0-1][0-9])-([0-3][0-9]) ([0-2][0-9]):([0-6][0-9]):([0-6][0-9]).([0-9][0-9][0-9])";
    public static final String DATE_T_TIME = "([0-9]{4})-([0-1][0-9])-([0-3][0-9])T([0-2][0-9]):([0-6][0-9]):([0-6][0-9])";
    public static final String DATE_TIME = "([0-9]{4})-([0-1][0-9])-([0-3][0-9]) ([0-2][0-9]):([0-6][0-9]):([0-6][0-9])";
    public static final String DATE_TIME_H = "([0-9]{4})-([0-1][0-9])-([0-3][0-9]) ([0-2][0-9]):([0-6][0-9])";
    public static final String DATE = "([0-9]{4})-([0-1][0-9])-([0-3][0-9])";
    public static final String START_TIME = " 00:00:00";
    public static final String END_TIME = " 23:59:59";
    public static final String TIME_H = ":00";
    public static final String[] parsePatterns = {"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss"};

    public static Date parseDate(String date, String fill) throws ParseException {
        return parseDate(date, fill, parsePatterns);
    }

    public static Date parseDate(String date, String fill, String... parsePatterns) throws ParseException {
        if (StringUtils.isNotBlank(date)) {
            if (date.matches(DateUtils.DATE)) {
                date += fill;
            }
            if (date.matches(DateUtils.DATE_TIME)) {
                return org.apache.commons.lang3.time.DateUtils.parseDate(date, parsePatterns);
            } else {
                throw new ParseException("时间格式错误", -1);
            }
        }
        return null;
    }

    public static LocalDateTime parseLocalDate(String date) throws ParseException {
        if (StringUtils.isNotBlank(date)) {
            if (date.matches(DateUtils.DATE)) {
                return localDateTime(date);
            } else {
                throw new ParseException("时间格式错误", -1);
            }
        }
        return null;
    }

    public static LocalDateTime parseLocalDateTime(String date, String fill) throws ParseException {
        return parseLocalDateTime(date, fill, "");
    }

    public static LocalDateTime parseLocalDateTime(String date, String fill, String millisecond) throws ParseException {
        if (StringUtils.isNotBlank(date)) {
            if (date.matches(DateUtils.DATE)) {
                date += fill;
            }
            if (date.matches(DateUtils.DATE_TIME_H)) {
                date += TIME_H;
            }

            if (date.matches(DATE_T_TIME_K)) {
                return localDateTime(date);
            }

            if (date.matches(DateUtils.DATE_TIME)) {
                return localDateTime(date + millisecond);
            } else {
                throw new ParseException("时间格式错误", -1);
            }
        }
        return null;
    }

    public static Date date(String dateStr) {
        DateFormat sdf = null;
        if (dateStr.matches(DATE_T_TIME_Z)) {
            dateStr = dateStr.substring(0, dateStr.length() - 1);
            dateStr = dateStr.split("T")[0] + " " + dateStr.split("T")[1];
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        } else if (dateStr.matches(DATE_T_TIME)) {
            dateStr = dateStr.split("T")[0] + " " + dateStr.split("T")[1];
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        } else if (dateStr.matches(DATE_TIME)) {
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        } else if (dateStr.matches(DATE)) {
            sdf = new SimpleDateFormat("yyyy-MM-dd");
        }

        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static LocalDateTime localDateTime(String dateStr) {
        DateTimeFormatter sdf = null;

        if (dateStr.matches(DATE_T_TIME_Z)) {
            dateStr = dateStr.substring(0, dateStr.length() - 1);
            dateStr = dateStr.split("T")[0] + " " + dateStr.split("T")[1];
            sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        } else if (dateStr.matches(DATE_T_TIME)) {
            dateStr = dateStr.split("T")[0] + " " + dateStr.split("T")[1];
            sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        } else if (dateStr.matches(DATE_T_TIME_K)) {
            sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        } else if (dateStr.matches(DATE_TIME)) {
            sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        } else if (dateStr.matches(DATE)) {
            sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        }

        return LocalDateTime.parse(dateStr, sdf);
    }

    public static LocalDateTime todayMin() {
        return LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
    }

    public static LocalDateTime todayMax() {
        return LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
    }

    public static LocalDateTime monthMin() {
        return LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth());
    }

    public static LocalDateTime monthMax() {
        return LocalDateTime.now().with(TemporalAdjusters.lastDayOfMonth());
    }
}
