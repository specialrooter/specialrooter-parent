package io.specialrooter.plus.elasticsearch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Ai
 */
@Slf4j
@Configuration
public class ElasticsearchConfig {

    @Bean
    public ElasticsearchTemplate elasticsearchTemplate(){
        return new ElasticsearchTemplate();
    }
}
