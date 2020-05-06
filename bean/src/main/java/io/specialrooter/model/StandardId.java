package io.specialrooter.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("标准ID")
@Data
public class StandardId {
    @ApiModelProperty(value = "ID",required = true)
    private String id;
}
