package io.specialrooter.plus.mybatisplus.handler;

import org.springframework.context.annotation.Bean;

/**
 * @Author Ai
 */
public class SnowflakeIdGeneratorConfig {
    @Bean
    public SnowflakeIdGenerator snowflakeIdGenerator(){
        return new SnowflakeIdGenerator(0,0);
    }
    @Bean
    public IdStrategyGenerator idStrategyGenerator(){
        return new IdStrategyGenerator();
    }

    @Bean
    public SupportMetaObjectHandler supportMetaObjectHandler(){
        return new SupportMetaObjectHandler();
    }
}
