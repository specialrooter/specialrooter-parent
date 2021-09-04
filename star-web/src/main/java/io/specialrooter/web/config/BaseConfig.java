package io.specialrooter.web.config;

import io.specialrooter.context.SpringContext;
import io.specialrooter.web.aspect.LogAspect;
import io.specialrooter.web.handler.TerminateConsulHandler;
import io.specialrooter.web.util.AppTypeUtil;
import org.springframework.context.annotation.Bean;

/**
 * 基础配置
 *
 * @author Ai
 */
public class BaseConfig {
    @Bean
    public SpringContext springContext() {
        return new SpringContext();
    }

    @Bean
    public LogAspect logAspect() {
        return new LogAspect();
    }

    // Consul元数据配置
//    @Bean
//    public ConsulConfigPostProcessor consulConfigPostProcessor(){
//        return new ConsulConfigPostProcessor();
//    }

    // eureka 增强配置


//    @Bean
//    public MyDataCenterInstanceConfig2 myDataCenterInstanceConfig(){
//        return new MyDataCenterInstanceConfig2();
//    }
//    @Profile("!default")
//    public EurekaInstanceConfigBean eurekaInstanceConfigBean(InetUtils inetUtils) {
//        EurekaInstanceConfigBean b = new EurekaInstanceConfigBean(inetUtils);
//
//        //扩展元数据-开发者信息
//        String name = LocalGitExecutor.name();
//        String email = LocalGitExecutor.email();
//        if(StringUtils.isNotBlank(name)){
//            b.getMetadataMap().put("name",name);
//        }
//
//        if(StringUtils.isNotBlank(email)){
//            b.getMetadataMap().put("email",email);
//        }
//
//        if(StringUtils.isNotBlank(email)){
//            b.getMetadataMap().put("ip",b.getIpAddress());
//        }
//        return b;
//    }


//    @Bean
//    public MyEurekaInstanceConfigBean eurekaInstanceConfigBean(InetUtils inetUtils){
//        return new MyEurekaInstanceConfigBean(inetUtils);
//    }

    @Bean
    public TerminateConsulHandler terminateConsulHandler() {
        return new TerminateConsulHandler();
    }

    @Bean
    public AppTypeUtil appTypeUtil() {
        return new AppTypeUtil();
    }

//    @Bean
//    public DictTemplate dictTemplate() {
//        return new DictTemplate();
//    }

}
