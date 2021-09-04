package io.specialrooter.util;

import com.google.common.base.CaseFormat;

/**
 * 命名转换
 */
public class GuavaUtils {
    /**
     * 驼峰转下划线命名
     * @param hump
     * @return
     */
    public static String humpToColumn(String hump){
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, hump);
    }

    /**
     * 驼峰转类名
     * @param hump
     * @return
     */
    public static String humpToClass(String hump){
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL,hump);
    }

    /**
     * 下划线命名转驼峰
     * @param column
     * @return
     */
    public static String columnToHump(String column){
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL,column);
    }
}
