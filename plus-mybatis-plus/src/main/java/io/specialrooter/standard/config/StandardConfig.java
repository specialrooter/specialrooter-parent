package io.specialrooter.standard.config;

import io.specialrooter.context.util.ApiUtils;
import io.specialrooter.plus.mybatisplus.aspect.LambdaUpdateWrapperAspect;
import io.specialrooter.standard.component.log.PrettyLoggersCloudHandler;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;

@ComponentScan("io.specialrooter.standard.component")
@MapperScan("io.specialrooter.standard.component.mapper")
public class StandardConfig {

    @Bean
    public ApiUtils apiUtils(){
        return new ApiUtils();
    }
    @Bean
    public LambdaUpdateWrapperAspect lambdaUpdateWrapperAspect(){
        return new LambdaUpdateWrapperAspect();
    }
}
