package io.specialrooter.plus.mybatisplus.camelcase;

import io.specialrooter.plus.mybatisplus.tenant.TenantConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Ai
 * @since 5.2.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({MapCamelCaseWrapperConfig.class, TenantConfig.class})
public @interface EnableMybatisPlusMapCamelCase {
}
