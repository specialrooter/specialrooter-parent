package io.specialrooter.plus.mybatisplus.camelcase;

import com.google.common.base.CaseFormat;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.wrapper.MapWrapper;

import java.util.Map;

/**
 * 继承类 MapWrapper,重写findProperty,通过useCamelCaseMapping来判断是否开启使用驼峰
 * @Author Ai
 */
public class MapCamelCaseWrapper extends MapWrapper {
    public MapCamelCaseWrapper(MetaObject metaObject, Map<String, Object> map) {
        super(metaObject, map);
    }

    /**
     * 判断是否启用了驼峰命名法的属性，启用则调用转换
     * @param name
     * @param useCamelCaseMapping
     * @return
     */
    @Override
    public String findProperty(String name, boolean useCamelCaseMapping) {
        if(useCamelCaseMapping){
            return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL,name);
        }
        return name;
    }
}
