package io.specialrooter.web.annotation;

import io.specialrooter.plus.mybatisplus.EnableMybatisPlusUltimate;
import io.specialrooter.web.config.*;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({CorsFilterConfig.class, SwaggerUIConfig.class, BaseConfig.class, RedisConfiguration.class, LocalDateTimeSerializerConfig.class})
@EnableMybatisPlusUltimate
public @interface SpecialRooterApplication {
}
