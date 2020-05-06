package io.specialrooter.plus.jackson;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME)
@Target(value = {TYPE})
public @interface DictSentry {
    String label();
    String value() default "id";
}
