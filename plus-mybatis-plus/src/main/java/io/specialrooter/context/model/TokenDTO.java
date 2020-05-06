package io.specialrooter.context.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("返回TOKEN对象")
public class TokenDTO {
    @ApiModelProperty("用户ID")
    protected Long id;
    @ApiModelProperty("应用全局编号")
    protected String appGid;
    @ApiModelProperty("应用身份令牌")
    protected String appToken;
    @ApiModelProperty("功能全局编号")
    protected String funcGid;
    @ApiModelProperty("功能身份令牌")
    protected String funcToken;
    @ApiModelProperty("用户全局编号")
    protected String userGid;
    @ApiModelProperty("用户身份令牌")
    protected String userToken;
    @ApiModelProperty(value = "全局TOKEN创建时间")
    protected String createTokenTime;

    @ApiModelProperty(value = "登录IP")
    protected String loginIp;

    @ApiModelProperty(value = "登录设备ID")
    protected String loginDevice;
    @ApiModelProperty(value = "登录设备名称")
    protected String loginDeviceName;

    @ApiModelProperty(value = "登录系统平台")
    protected Integer loginPlatform;

    @ApiModelProperty(value = "用户登录地区")
    protected Integer loginRegion;
}
