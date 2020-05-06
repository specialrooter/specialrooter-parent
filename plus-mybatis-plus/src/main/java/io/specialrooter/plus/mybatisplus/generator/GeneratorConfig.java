package io.specialrooter.plus.mybatisplus.generator;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;

/**
 * @author Ai
 */
@MapperScan("io.specialrooter.mapper")
public class GeneratorConfig {
    @Bean
    public CodeGenerator codeGenerator(){
        return new CodeGenerator();
    }

    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

}
