package io.specialrooter.standard.component.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 *
 *@ClassName InterfaceVO
 *@Author lubowen
 *@Date 2020/11/19 15:34
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "InterfaceVO", description = "接口列表")
public class InterfaceVO {

    @ApiModelProperty(value = "路径")
    private String url;

    @ApiModelProperty(value = "类名")
    private String className;

    @ApiModelProperty(value = "接口说明")
    private String apiOperationValue;

    @ApiModelProperty(value = "接口发布说明")
    private String apiOperationNotes;

    @ApiModelProperty(value = "方法请求类型")
    private String type;
}
