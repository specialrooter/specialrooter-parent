package io.specialrooter.plus.mybatisplus.converter;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import io.specialrooter.context.functional.Functional;
import io.specialrooter.plus.mybatisplus.util.DataUtils;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DomainConverter {


    public static <T> List<T> convert(Class<T> clazz, List<?> list) {
        return convert(clazz, list, e -> convert(e, clazz));
    }

    public static <T> List<T> convert(List<?> list, Class<T> clazz, Functional.Callback<T> callback) {
        return convert(clazz, list, e -> {
            T convert = convert(e, clazz);
            callback.doWith((T) convert);
            return convert;
        });
    }

    /**
     * 列表转换
     *
     * @param clazz the clazz
     * @param list  the list
     */
    public static <T> List<T> convert(Class<T> clazz, List<?> list, Function<Object, T> convert) {
        return CollectionUtils.isEmpty(list) ? Collections.emptyList() : list.stream().map(convert).collect(Collectors.toList());
    }

    /**
     * 单个对象转换
     *
     * @param targetClass 目标对象
     * @param source      源对象
     * @return 转换后的目标对象
     */
    public static <T> T convert(Object source, Class<T> targetClass) {
        return DataUtils.copyProperties(source, targetClass);
    }

    /**
     * 单个对象转换
     *
     * @param targetClass 目标对象
     * @param source      源对象
     * @return 转换后的目标对象
     */
    public static <T> T convert(Object source, Class<T> targetClass, Functional.Callback<T> callback) {
        T o = DataUtils.copyProperties(source, targetClass);
        if (callback != null) callback.doWith((T) o);
        return o;
    }

}
