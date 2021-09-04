package io.specialrooter.plus.mybatisplus.tenant;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)//注意用这个注解才能在运行时使用反射
@Target({ElementType.TYPE})
@Documented
@Import({TenantScanRegister.class})
public @interface TenantScan {
    //扫描包路径
    String[] basePackages() default {};
    //扫描类
    Class<?>[] basePackageClasses() default {};
}
