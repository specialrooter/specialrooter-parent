package io.specialrooter.holiday;

import com.alibaba.fastjson.JSONObject;
import io.specialrooter.http.HttpRequestUtils;

import java.time.LocalDate;
import java.util.*;

public class TimorHoliday implements StatutoryHoliday {
    private final String api = "http://timor.tech/api/holiday/year";

    @Override
    public List<Holiday> holidays() {
        String result = HttpRequestUtils.doGet(api);
        Map map = JSONObject.parseObject(result, Map.class);
        if (map != null) {
            Integer code = (Integer) map.get("code");
            if (code == 0) {
                JSONObject holidayJson = (JSONObject) map.get("holiday");
                Set<Map.Entry<String, Object>> entrySet = holidayJson.entrySet();
                List<Holiday> holidayList = new ArrayList<>();
                for (Map.Entry<String, Object> entry : entrySet) {
                    JSONObject holidayObj = JSONObject.parseObject(entry.getValue().toString());
                    Holiday holiday = new Holiday();
                    holiday.setName(holidayObj.getString("name"));
                    holiday.setAfter(holidayObj.getBooleanValue("after"));
                    holiday.setDate(LocalDate.parse(holidayObj.getString("date")));
                    holidayList.add(holiday);
                }
                return holidayList;
            }
        }
        return null;
    }

    @Override
    public Set<LocalDate> holidaySet() {
        String result = HttpRequestUtils.doGet(api);
        Map map = JSONObject.parseObject(result, Map.class);
        if (map != null) {
            Integer code = (Integer) map.get("code");
            if (code == 0) {
                JSONObject holidayJson = (JSONObject) map.get("holiday");
                Set<Map.Entry<String, Object>> entrySet = holidayJson.entrySet();
                Set<LocalDate> holidaySet = new HashSet<>();
                for (Map.Entry<String, Object> entry : entrySet) {
                    JSONObject holidayObj = JSONObject.parseObject(entry.getValue().toString());
                    holidaySet.add(LocalDate.parse(holidayObj.getString("date")));
                }
                return holidaySet;
            }
        }
        return null;
    }
}
