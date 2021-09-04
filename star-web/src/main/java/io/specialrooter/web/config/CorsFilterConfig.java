package io.specialrooter.web.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

//@Configuration
public class CorsFilterConfig {

    @Bean
    public FilterRegistrationBean corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        //config.addAllowedOrigin("http://localhost:9000");
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedHeader("fileDownload");
        config.addAllowedHeader("filedownload");
        config.addAllowedMethod("*");
        // CORS 配置对所有接口都有效
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(0);
        System.out.println("cors config enable!");
        return bean;
    }
}
