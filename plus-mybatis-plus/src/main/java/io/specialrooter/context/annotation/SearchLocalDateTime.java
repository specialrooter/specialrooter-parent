package io.specialrooter.context.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.specialrooter.plus.mybatisplus.x.LocalDateTimeJsonDeserializer;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonDeserialize(using = LocalDateTimeJsonDeserializer.class)
@Deprecated
public @interface SearchLocalDateTime {
    DateFill fill() default DateFill.START_TIME;
}
