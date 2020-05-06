package io.specialrooter.plus.mybatisplus.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtils {
    public static final String DATE_T_TIME_Z = "([0-9]{4})-([0-1][0-9])-([0-3][0-9])T([0-2][0-9]):([0-6][0-9]):([0-6][0-9]).([0-9][0-9][0-9])Z";
    public static final String DATE_T_TIME = "([0-9]{4})-([0-1][0-9])-([0-3][0-9])T([0-2][0-9]):([0-6][0-9]):([0-6][0-9])";
    public static final String DATE_TIME = "([0-9]{4})-([0-1][0-9])-([0-3][0-9]) ([0-2][0-9]):([0-6][0-9]):([0-6][0-9])";
    public static final String DATE = "([0-9]{4})-([0-1][0-9])-([0-3][0-9])";

    public static Date date(String dateStr){
        DateFormat sdf = null;
        if(dateStr.matches(DATE_T_TIME_Z)){
            dateStr = dateStr.substring(0,dateStr.length()-1);
            dateStr = dateStr.split("T")[0]+" "+dateStr.split("T")[1];
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        }else if(dateStr.matches(DATE_T_TIME)){
            dateStr = dateStr.split("T")[0]+" "+dateStr.split("T")[1];
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        }else if(dateStr.matches(DATE_TIME)){
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        }else if(dateStr.matches(DATE)){
            sdf = new SimpleDateFormat("yyyy-MM-dd");
        }

        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static LocalDateTime localDateTime(String dateStr){
        DateTimeFormatter sdf = null;

        if(dateStr.matches(DATE_T_TIME_Z)){
            dateStr = dateStr.substring(0,dateStr.length()-1);
            dateStr = dateStr.split("T")[0]+" "+dateStr.split("T")[1];
            sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        }else if(dateStr.matches(DATE_T_TIME)){
            dateStr = dateStr.split("T")[0]+" "+dateStr.split("T")[1];
            sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        }else if(dateStr.matches(DATE_TIME)){
            sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        }else if(dateStr.matches(DATE)){
            sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        }

        return LocalDateTime.parse(dateStr,sdf);
    }
}
