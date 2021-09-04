package io.specialrooter.context.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.specialrooter.plus.mybatisplus.x.SearchDateDeserializer;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonDeserialize(using = SearchDateDeserializer.class)
public @interface SearchDate {
    DateFill fill() default DateFill.START_TIME;
}
