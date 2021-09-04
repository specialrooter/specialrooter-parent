package io.specialrooter.plus.mybatisplus.model;


import io.specialrooter.plus.mybatisplus.converter.DomainConverter;

import java.io.Serializable;


public class SupperClass implements Serializable {

    private static final long serialVersionUID = 2059127567719483387L;

    public <T> T convert(Class<T> clazz) {
        return DomainConverter.convert(this, clazz);
    }
}
































































