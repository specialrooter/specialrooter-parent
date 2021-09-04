package io.specialrooter.web.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "MessagePool", description = "消息池入参表")
public class MessagePool {

    @ApiModelProperty(value = "租户ID")
    private Long tenantId;

    @ApiModelProperty(value = "消息枚举值")
    private Integer enumNum;

    @ApiModelProperty(value = "消息内容")
    private String content;
}
