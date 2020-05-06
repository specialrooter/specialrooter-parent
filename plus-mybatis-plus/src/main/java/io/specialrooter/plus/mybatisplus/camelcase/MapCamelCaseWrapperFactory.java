package io.specialrooter.plus.mybatisplus.camelcase;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.wrapper.ObjectWrapper;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;

import java.util.Map;

/**
 * 通过包装工厂来创建自定义的包装类,通过hasWrapperFor判断参数不为空,并且类型是Map的时候才使用自己扩展的ObjectWrapper
 * @Author Ai
 */
public class MapCamelCaseWrapperFactory implements ObjectWrapperFactory {
    @Override
    public boolean hasWrapperFor(Object object) {
        return object != null && object instanceof Map;
    }

    @Override
    public ObjectWrapper getWrapperFor(MetaObject metaObject, Object object) {
        return new MapCamelCaseWrapper(metaObject,(Map)object);
    }
}
