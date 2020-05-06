package io.specialrooter.web.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value="标准请求模型")
@Data
public class RequestModel<T> {

    @ApiModelProperty(value = "当前平台编号",required = true)
    private String platformId;
    @ApiModelProperty(value = "当前APP类型编号",required = true)
    private String appTypeId;
    @ApiModelProperty(value = "当前页面编号",required = true)
    private String currPageId;
    @ApiModelProperty(value = "当前用户TOKEN",required = true)
    private String currUserTokenData;
    @ApiModelProperty(value = "当前菜单编号",required = true)
    private String menuId;
    @ApiModelProperty(value = "当前指令编号",required = true)
    private String commandId;
    @ApiModelProperty(value = "业务数据请求",required = true)
    private T cmdPostData;
}
