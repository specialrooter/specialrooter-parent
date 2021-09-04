package io.specialrooter.context.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.specialrooter.plus.mybatisplus.x.LocalDateTimeDeserializer;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonDeserialize(using = LocalDateTimeDeserializer.class)
public @interface JsonFormatLocalDateTime {
    DateFill fill() default DateFill.START_TIME;
}
