package io.specialrooter.plus.mybatisplus;

import io.specialrooter.plus.mybatisplus.camelcase.EnableMybatisPlusMapCamelCase;
import io.specialrooter.standard.config.StandardConfig;
import io.specialrooter.plus.mybatisplus.generator.EnableMyBatisPlusGenerator;
import io.specialrooter.plus.mybatisplus.handler.EnableMyBatisPlusSnowflake;
import io.specialrooter.standard.config.ThreadLocalConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Ai
 * @since 5.2.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@EnableMyBatisPlusSnowflake
@EnableMybatisPlusMapCamelCase
@EnableMyBatisPlusGenerator
//@EnableJacksonPlus
@Import({StandardConfig.class, ThreadLocalConfiguration.class})
public @interface EnableMybatisPlusUltimate {
}
