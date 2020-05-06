package io.specialrooter.plus.jackson;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Ai
 */
@Documented
@Retention(RUNTIME)
@Target(value = { METHOD,FIELD,PARAMETER})
@Repeatable(value= JsonPlusFilters.class)
public @interface JsonPlusFilter {

    Class<?> value();

    /**
     * include为对象需要包含的字段，默认使用include，如果include为空，则使用exclude字段
     * @return
     */
    String[] include() default {};

    /**
     * include 为对象需要包含的字段，扩展@JsonView 使用包装类封装后返回空异常
     * @return
     */
    Class<?>[] includeClass() default {};

    /**
     * exclude 要排除的字段
     * @return
     */
    String[] exclude() default {};

    /**
     * include 为对象需要包含的字段，扩展@JsonView 使用包装类封装后返回空异常
     * @return
     */
    Class<?>[] excludeClass() default {};


    FilterType type() default  FilterType.RESPONSE;

    enum FilterType {
        REQUEST, RESPONSE;
    }
}
