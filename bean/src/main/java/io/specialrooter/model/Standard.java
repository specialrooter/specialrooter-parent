package io.specialrooter.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@ApiModel("标准查询")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Standard {
    @ApiModelProperty(value = "字段名",required = true)
    private String column;
    @ApiModelProperty(value = "字段值",required = true)
    private Object value;
}
