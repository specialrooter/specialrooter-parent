package io.specialrooter.plus.jackson;

import io.specialrooter.plus.elasticsearch.EnableElasticsearchPlus;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Ai
 * @since 5.2.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({JsonPlusConfig.class})
public @interface EnableJacksonPlus {
}
