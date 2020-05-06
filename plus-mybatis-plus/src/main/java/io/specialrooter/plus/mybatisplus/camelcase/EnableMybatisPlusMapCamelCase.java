package io.specialrooter.plus.mybatisplus.camelcase;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Ai
 * @since 5.2.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({MapCamelCaseWrapperConfig.class})
public @interface EnableMybatisPlusMapCamelCase {
}
