package io.specialrooter.plus.mybatisplus.tenant;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

import java.util.Set;

public class TenantScanner extends ClassPathBeanDefinitionScanner {
    public TenantScanner(BeanDefinitionRegistry registry) {
        super(registry, false);
    }

    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        //添加过滤条件，这里只扫描继承了SaaS类的Bean
        addIncludeFilter(new TenantScanFilter());
        return super.doScan(basePackages);
    }
}
