package io.specialrooter.plus.mybatisplus.handler;

import io.specialrooter.plus.mybatisplus.basic.Constant;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Ai
 * @since 5.2.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({SnowflakeIdGeneratorConfig.class, Constant.class})
public @interface EnableMyBatisPlusSnowflake {
}
