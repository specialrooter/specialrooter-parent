package io.specialrooter.web.config;

import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import io.specialrooter.plus.mybatisplus.handler.OauthRedisHandler;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * auto Configuration
 *
 * @ClassName HttpStarterConfig
 * @Author tian_ye
 * @Date 2019/11/22 10:59
 */
@EnableFeignClients(basePackages = {"com.starlink.**.feign","com.eascs.scm.**.feign"})
public class SpecialRooterFeignConfig {
//    @Bean
//    @ConditionalOnMissingBean(HystrixConcurrencyStrategy.class)
//    public HystrixConcurrencyStrategy hystrixConcurrencyStrategy() {
//        return new FeignHystrixConcurrencyStrategyConfig();
//    }
}
