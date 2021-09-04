package io.specialrooter.web.annotation;

import io.specialrooter.plus.mybatisplus.EnableMybatisPlusUltimate;
import io.specialrooter.web.config.*;
import io.specialrooter.web.processor.EurekaInstanceUltimateConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({CorsFilterConfig.class, SwaggerUIConfig.class, BaseConfig.class, RedisConfiguration.class,
        LocalDateTimeSerializerConfig.class,SpecialRooterFeignConfig.class, EurekaInstanceUltimateConfig.class})
@EnableMybatisPlusUltimate
@EnableConfigurationProperties({XxlExecutorConfig.class})
public @interface SpecialRooterApplication {
}
