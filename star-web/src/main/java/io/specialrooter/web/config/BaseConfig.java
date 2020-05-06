package io.specialrooter.web.config;

import io.specialrooter.context.SpringContext;
import io.specialrooter.web.core.DictTemplate;
import org.springframework.context.annotation.Bean;

/**
 * 基础配置
 *
 * @author Ai
 */
public class BaseConfig {
    @Bean
    public SpringContext springContext(){
        return new SpringContext();
    }

    @Bean
    public DictTemplate dictTemplate() {
        return new DictTemplate();
    }

}
