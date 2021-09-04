package io.specialrooter.context.ifc;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.specialrooter.plus.mybatisplus.x.EnumDeserializer;

/**
 * 自定义枚举反序列化接口
 *
 * @param <K>
 * @param <V>
 */
@JsonDeserialize(using = EnumDeserializer.class)
public interface IEnum<K, V> {
    K getCode();
    V getMsg();

    static <E extends Enum<E> & IEnum> E valueOf(String enumCode,Class<E> clazz) {
        E enum_ = Enum.valueOf(clazz, enumCode);
        return enum_;
    }
}