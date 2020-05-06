package io.specialrooter.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel("标准对象")
@Data
public class StandardBean<T> {
    private T data;
}
