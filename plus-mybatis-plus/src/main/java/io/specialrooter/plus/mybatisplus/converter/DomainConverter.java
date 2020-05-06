package io.specialrooter.plus.mybatisplus.converter;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import io.specialrooter.plus.mybatisplus.util.DataUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DomainConverter {

    /**
     * 列表转换
     *
     * @param clazz the clazz
     * @param list  the list
     */
    public static <T> List<T> convert(Class<T> clazz, List<?> list) {
        return CollectionUtils.isEmpty(list) ? Collections.emptyList() : list.stream().map(e -> convert(e,clazz )).collect(Collectors.toList());
    }

    /**
     * 单个对象转换
     *
     * @param targetClass 目标对象
     * @param source      源对象
     * @return 转换后的目标对象
     */
    public static <T> T convert(Object source,Class<T> targetClass) {
        return DataUtils.copyProperties(source,targetClass);
    }
}
