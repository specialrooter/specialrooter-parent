package io.specialrooter.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("标准IDs")
@Data
public class StandardIdBatch {
    @ApiModelProperty(value = "IDs",required = true)
    private List<String> id;
}
