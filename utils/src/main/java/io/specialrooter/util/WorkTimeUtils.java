package io.specialrooter.util;

import io.specialrooter.holiday.Holiday;
import io.specialrooter.holiday.TimorHoliday;

import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 时间工具类
 */
public class WorkTimeUtils {


    /**
     * 获取两个日期之间有效工作时间
     */
    public static BigDecimal workTime(LocalDateTime begin, LocalDateTime end, Set<LocalDate> holiday, Set<LocalDate> workday, Set<LocalTime> workTime) {
        // 先计算两个时间历史天数
//        Period period = Period.between(begin.toLocalDate(), end.toLocalDate());
//        System.out.println(period.getDays());
        long distance = ChronoUnit.DAYS.between(begin, end);
//        System.out.println(distance);
//        List<LocalDateTime> days = Stream.iterate(begin,  d -> d.plusDays(1)).limit(distance + 1).collect(Collectors.toList());
//        System.out.println(days);
        List<LocalDateTime> days = new ArrayList<>();
        for (long i = 0; i < distance; i++) {
            LocalDateTime d = begin.plusDays(i);
            // 排除法定节假日
            if (!holiday.contains(d.toLocalDate())) {
                // 识别周末
                if (d.getDayOfWeek() == DayOfWeek.SATURDAY || d.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    if(workday!=null){
                        // 周末加班
                        if(workday.contains(d.toLocalDate())){
                            days.add(d);
                        }
                    }
                }else{
                    days.add(d);
                }
            }
        }

        // 时分秒

        // days.size()*6+

         days.forEach(day-> System.out.println(day));

        LocalDateTime lastDateTime = days.get(days.size() - 1);

        // 当天
        LocalDateTime localDateTime = lastDateTime.plusDays(1);
        if(localDateTime.isBefore(end)){
            long between = ChronoUnit.SECONDS.between(localDateTime, end);
            System.out.println(between);
        }

        //6*60*60


//        TemporalAdjuster nextWorkingDay = TemporalAdjusters.ofDateAdjuster(
//                temporal -> {
//                    DayOfWeek dow =
//                            DayOfWeek.of(temporal.get(ChronoField.DAY_OF_WEEK));
//                    int dayToAdd = 1;
//                    if (dow == DayOfWeek.FRIDAY) dayToAdd = 3;
//                    if (dow == DayOfWeek.SATURDAY) dayToAdd = 2;
//                    return temporal.plus(dayToAdd, ChronoUnit.DAYS);
//                });
//        LocalDateTime localDate = start.with(nextWorkingDay);
//        System.out.println(localDate);
//        System.out.println(period.getDays());
//        System.out.println(end.toLocalTime());

        return BigDecimal.valueOf(1);
    }


    public static void main(String[] args) {
        // 提工结束日期
        LocalDateTime workTime1 = LocalDateTime.of(2021, 8, 1, 15, 30);
        // 现在
        LocalDateTime workTime2 = LocalDateTime.now();
        // 有效工作时间段

        // 获取法定节假日
        Set<LocalDate> holidaySet = new TimorHoliday().holidaySet();
        List<Holiday> holidays = new TimorHoliday().holidays();

        // 加班时间
        Set<LocalDate> workday = new HashSet<>();
        workday.add(LocalDate.of(2021,8,7));

        // 精确时分秒
        workTime(workTime1, workTime2, holidaySet, workday, null);
        //LocalDate start = LocalDate.of(2018, Month.DECEMBER, 1);
        //LocalDate end = LocalDate.of(2020, Month.APRIL, 10);

        //List<LocalDate> days = Stream.iterate(start, d -> d.plusDays(1)).limit(distance + 1).collect(toList());
    }

}
