//package io.specialrooter.web.processor;
//
//import io.specialrooter.web.exec.LocalGitExecutor;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.cloud.commons.util.InetUtils;
//import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Profile;
//
//public class EurekaMetaDataProcessor {
//
////    @Component
//
////    @Bean
////    @Profile("!default")
////    public EurekaInstanceConfigBean eurekaInstanceConfig(InetUtils inetUtils) {
////        EurekaInstanceConfigBean b = new EurekaInstanceConfigBean(inetUtils);
////
////        //扩展元数据-开发者信息
////        String name = LocalGitExecutor.name();
////        String email = LocalGitExecutor.email();
////        if(StringUtils.isNotBlank(name)){
////            b.getMetadataMap().put("name",name);
////        }
////
////        if(StringUtils.isNotBlank(email)){
////            b.getMetadataMap().put("email",email);
////        }
////
////        if(StringUtils.isNotBlank(email)){
////            b.getMetadataMap().put("ip",b.getIpAddress());
////        }
////        return b;
////    }
//}
