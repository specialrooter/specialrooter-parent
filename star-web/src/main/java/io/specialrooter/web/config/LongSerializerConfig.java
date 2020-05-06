//package io.specialrooter.web.config;
//
//
//import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
//import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
//import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
//import org.springframework.context.annotation.Bean;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//
////@Configuration
//public class LongSerializerConfig {
//    @Bean
//    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
//        return builder -> {
//            //设置 Long 规避精度丢失问题
//            builder.serializerByType(Long.class, ToStringSerializer.instance)
//                    .serializerByType(Long.TYPE, ToStringSerializer.instance);
//        };
//    }
//}
