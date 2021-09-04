package io.specialrooter.plus.mybatisplus.tenant;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantSqlParser;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.StringValue;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

//@Configuration
public class TenantConfig {

    /**
     * 新的分页插件,一缓和二缓遵循mybatis的规则,需要设置 MybatisConfiguration#useDeprecatedExecutor = false 避免缓存出现问题
     * paginationInterceptor
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();


//            interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new TenantLineHandler() {
//            @Override
//            public Expression getTenantId() {
//                RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
//                ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
//                HttpServletRequest request = servletRequestAttributes.getRequest();
////                Enumeration<String> headerNames = request.getHeaderNames();
////                while (headerNames.hasMoreElements()) {
////                    String header = request.getHeader(headerNames.nextElement());
////                    System.out.println(header);
////                }
//                String sc = request.getHeader("sc");
//                System.out.printf("sc::"+sc);
//                if(StringUtils.isNotEmpty(sc)){
//                    return new StringValue(sc);
//                }
//                return new NullValue();
//            }
//
//            // 这是 default 方法,默认返回 false 表示所有表都需要拼多租户条件
//            @Override
//            public boolean ignoreTable(String tableName) {
//                System.out.println(tableName);
//
//                boolean b = TenantScanFilter.domains.stream().anyMatch(
//                        (t) -> !t.equalsIgnoreCase(tableName)
//                );
//                return b;
//            }

//            /**
//             * operation_id 运营商ID header sc
//             * @return
//             */
//            @Override
//            public String getTenantIdColumn() {
//                return "operation_id";
//            }
//        }));
        // 如果用了分页插件注意先 add TenantLineInnerInterceptor 再 add PaginationInnerInterceptor
        // 用了分页插件必须设置 MybatisConfiguration#useDeprecatedExecutor = false


        // 多租户
        TenantLineInnerInterceptor tenantLineInnerInterceptor = new TenantLineInnerInterceptor();
        TenantLineHandler myTenantLineHandler = new MyTenantLineHandler();
        tenantLineInnerInterceptor.setTenantLineHandler(myTenantLineHandler);
        interceptor.addInnerInterceptor(tenantLineInnerInterceptor);

        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        paginationInnerInterceptor.setMaxLimit(-1L);

        // 分页插件
        interceptor.addInnerInterceptor(paginationInnerInterceptor);
        // 防止全表更新与删除插件
//        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        return interceptor;

    }


    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> configuration.setUseDeprecatedExecutor(Boolean.FALSE);
    }

    /**
     * 监听器：监听HTTP请求事件
     * 解决RequestContextHolder.getRequestAttributes()空指针问题
     *
     * @return
     */
    @Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }

    /**
     * 初始化Bean扫描，判断用于租户的表格
     * @return
     */
//    @Bean
//    public BeanEventProcessor beanEventProcessor(){
//        return new BeanEventProcessor();
//    }
}
