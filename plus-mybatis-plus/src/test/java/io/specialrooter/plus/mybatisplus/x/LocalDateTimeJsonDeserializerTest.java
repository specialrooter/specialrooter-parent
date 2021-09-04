package io.specialrooter.plus.mybatisplus.x;

import com.alipay.api.domain.Car;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.time.LocalDateTime;
import java.util.List;

public class LocalDateTimeJsonDeserializerTest {
    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("LocalDateTimeJsonDeserializer", new Version(3, 1, 8, null, null, null));
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeJsonDeserializer());
        objectMapper.registerModule(module);


    }
}
