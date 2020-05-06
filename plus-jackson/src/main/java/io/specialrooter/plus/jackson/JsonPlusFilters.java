package io.specialrooter.plus.jackson;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Ai
 */
@Documented
@Retention(RUNTIME)
@Target({ FIELD, METHOD,PARAMETER })
public @interface JsonPlusFilters {
    JsonPlusFilter[] value();
}
