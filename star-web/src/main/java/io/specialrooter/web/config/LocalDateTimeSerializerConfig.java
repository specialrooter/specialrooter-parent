package io.specialrooter.web.config;


import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//@Configuration
public class LocalDateTimeSerializerConfig {

    @Value("${spring.jackson.date-format:yyyy-MM-dd'T'HH:mm:ss.SSS'Z'}")
    private String pattern;

    private String pattern2="yyyy-MM-dd";

//    @Bean
//    public LocalDateTimeSerializer localDateTimeDeserializer() {
//        return new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(pattern));
//    }
//
//    @Bean
//    public LocalDateTimeDeserializer localDateTimeSerializer() {
//        return new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(pattern));
//    }
//
//    @Bean
//    public LocalDateSerializer localDateSerializer() {
//        return new LocalDateSerializer(DateTimeFormatter.ofPattern(pattern2));
//    }
//
//    @Bean
//    public LocalDateDeserializer localDateDeserializer() {
//        return new LocalDateDeserializer(DateTimeFormatter.ofPattern(pattern2));
//    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> {
            //设置时间格式
            builder.serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(pattern)));
            builder.serializerByType(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(pattern2)));

            builder.deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(pattern)));
            builder.deserializerByType(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(pattern2)));
//            //设置 Long 规避精度丢失问题
//            builder.serializerByType(Long.class, ToStringSerializer.instance)
//                    .serializerByType(Long.TYPE, ToStringSerializer.instance);
        };
    }
}
