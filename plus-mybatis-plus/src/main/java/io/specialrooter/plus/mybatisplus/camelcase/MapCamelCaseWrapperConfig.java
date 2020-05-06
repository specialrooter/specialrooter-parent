package io.specialrooter.plus.mybatisplus.camelcase;

import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import org.springframework.context.annotation.Bean;

/**
 * 扩展map-underscore-to-camel-case 属性，返回Map不能转换驼峰命名
 * 替换下,以前默认的实现,刚好,mybatis-spring-boot上面有告诉我们怎么做,返回一个 ConfigurationCustomizer 的bean,
 * 通过匿名内部类实现覆盖默认的MapWrapper的findProperty函数
 * @Author Ai
 */
//@Configuration
public class MapCamelCaseWrapperConfig {
    @Bean
    public ConfigurationCustomizer mybatisConfigurationCustomizer(){
        return configuration -> configuration.setObjectWrapperFactory(new MapCamelCaseWrapperFactory());
    }
}
