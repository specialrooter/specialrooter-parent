package io.specialrooter.context.annotation;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@ApiModel("查询字段注解")
public @interface Search {
    /**
     * 表别名
     *
     * @return
     */
    @ApiModelProperty("查询字段表别名，用于多表查询指定不同表相同字段")
    String tableAlias() default "";

    @ApiModelProperty("查询字段运算类型")
    SearchOption option() default SearchOption.AUTO;

    /**
     * 映射字段名
     *
     * @return
     */
    @ApiModelProperty("查询字段别名")
    String columnAlias() default "";

        /**
     * 映射字段名
     *
     * @return
     */
    @ApiModelProperty("驼峰命名转数据库命名")
    boolean hump() default true;

    @ApiModelProperty("是否启用查询，false则不会自动将字段纳入查询条件")
    boolean query() default true;

    /**
     * Between 时启用
     *
     * @return
     */
    @ApiModelProperty("特殊条件使用(查询条件类型为Between时)：一个查询字段值对应sql两个字段时使用：@Search(start = 'a.price1',end ='a.price2')")
    String start() default "";

    String end() default "";
}


