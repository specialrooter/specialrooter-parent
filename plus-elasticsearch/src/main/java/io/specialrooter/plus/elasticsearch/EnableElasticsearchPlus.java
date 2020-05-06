package io.specialrooter.plus.elasticsearch;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Ai
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({ElasticsearchConfig.class})
public @interface EnableElasticsearchPlus {
}
