package io.specialrooter.plus.jackson;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME)
@Target(value = {METHOD, FIELD})
public @interface Dict {
    String value();
    String mapper() default "";
    Dict.Result res() default Dict.Result.DDT;

    enum Result {
        DDO, DDT,DDL;
    }
}
