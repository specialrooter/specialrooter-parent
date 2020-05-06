package io.specialrooter.plus.mybatisplus.model;


import io.specialrooter.plus.mybatisplus.converter.DomainConverter;

import java.io.Serializable;


public class SupperClass implements Serializable {
    public <T> T convert(Class<T> clazz) {
        return DomainConverter.convert(this, clazz);
    }
}
































































