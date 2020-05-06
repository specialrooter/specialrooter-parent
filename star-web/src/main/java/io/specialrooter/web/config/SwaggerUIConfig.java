package io.specialrooter.web.config;

import io.specialrooter.swagger.ui.annotations.EnableSwaggerSpecialRooterUI;
import io.specialrooter.web.util.SwaggerConfigUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


/**
 * @author Ai
 */
//@Configuration
@EnableSwagger2
@EnableSwaggerSpecialRooterUI
public class SwaggerUIConfig implements WebMvcConfigurer {

    @Bean
    public Docket generatorAPI() {
        return SwaggerConfigUtils.docket("代码生成-工具", "io.specialrooter.standard.component.controller");
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
