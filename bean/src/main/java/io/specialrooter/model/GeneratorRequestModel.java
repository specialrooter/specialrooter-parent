package io.specialrooter.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Ai
 */
@ApiModel("自动成成代码参数接口")
@Data
public class GeneratorRequestModel {
    @ApiModelProperty(value = "表名",required = true)
    private String table;
    @ApiModelProperty(value = "GitLab账号",required = true)
    private String author;
    @ApiModelProperty(value = "数据源名称")
    private String datasource;
}
