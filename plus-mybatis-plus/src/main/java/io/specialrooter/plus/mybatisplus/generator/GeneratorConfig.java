package io.specialrooter.plus.mybatisplus.generator;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.optimize.JsqlParserCountOptimize;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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

    /**
     * 开启多租户兼容模式
     * 如果定时多租户，以多租户为准，如果没有则以默认为准
     * @return
     */
//    @Bean
//    @ConditionalOnMissingBean(PaginationInterceptor.class)
//    public PaginationInterceptor paginationInterceptor() {
//        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
//        paginationInterceptor.setCountSqlParser(new JsqlParserCountOptimize(true));
//        return paginationInterceptor;
//    }

}
