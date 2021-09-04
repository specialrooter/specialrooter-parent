package io.specialrooter.plus.mybatisplus.handler;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * @Author Ai
 */
public class SnowflakeIdGeneratorConfig {
    @Bean
    public SnowflakeIdGenerator snowflakeIdGenerator() {
        return new SnowflakeIdGenerator();
    }

    @Bean
    public IdStrategyGenerator idStrategyGenerator() {
        return new IdStrategyGenerator();
    }

    @Bean
    public SupportMetaObjectHandler supportMetaObjectHandler(){
        return new SupportMetaObjectHandler();
    }


    /**
     * 备用模式,如果没有OauthRedisHandler实例化对象则使用这个默认的OauthRedisDefaultHandler实现类
     *
     * @return
     */
    @Bean
    /**
     * 注：
     * @Conditional(TestCondition.class)
     * @ConditionalOnBean（仅仅在当前上下文中存在某个对象时，才会实例化一个Bean）
     * @ConditionalOnClass（某个class位于类路径上，才会实例化一个Bean）如果Class存在才会实例
     * @ConditionalOnExpression（当表达式为true的时候，才会实例化一个Bean）
     * @ConditionalOnMissingBean（仅仅在当前上下文中不存在某个对象时，才会实例化一个Bean）
     * @ConditionalOnMissingClass（某个class类路径上不存在的时候，才会实例化一个Bean）
     * @ConditionalOnNotWebApplication（不是web应用）
     */
    @ConditionalOnMissingBean(OauthRedisHandler.class)
    public OauthRedisDefaultHandler oauthRedisDefaultHandler() {
        return new OauthRedisDefaultHandler();
    }
}
