package io.specialrooter.plus.mybatisplus.generator;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Ai
 * @since 5.2.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({GeneratorConfig.class})
public @interface EnableMyBatisPlusGenerator {
}
