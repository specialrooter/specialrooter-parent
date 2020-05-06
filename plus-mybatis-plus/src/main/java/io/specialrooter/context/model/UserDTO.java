package io.specialrooter.context.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 登录用户信息
 * </p>
 *
 * @author xinya.hou
 * @since 2019-07-16
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "当前用户LoginUser对象", description = "登录用户信息")
public class UserDTO {

    protected static final long serialVersionUID = 1L;
    @ApiModelProperty("ID")
    protected Long id;
    @ApiModelProperty("排序编号")
    protected Long sortId;
    @ApiModelProperty("删除状态")
    protected Boolean stateDeleted;
    @ApiModelProperty("停用状态")
    protected Boolean statePaused;
    @ApiModelProperty("锁定状态")
    protected Boolean stateLocked;

    @ApiModelProperty(value = "登录账号")
    protected String name;

    @ApiModelProperty(value = "用户编码")
    protected String userCode;

    @ApiModelProperty(value = "手机号码")
    protected String cellphone;

    @ApiModelProperty(value = "电子邮件")
    protected String email;

    @ApiModelProperty(value = "微信OPENID")
    protected String wxOpenId;

    @ApiModelProperty(value = "微信UNIONID")
    protected String wxUnionId;

    @ApiModelProperty(value = "用户类型(字典.用户类型)")
    protected Integer userType;
    @ApiModelProperty(value = "审核状态(字典.审核状态)")
    protected Integer checkState;
    @ApiModelProperty(value = "地域编码")
    protected Integer regionCode;

    @ApiModelProperty(value = "组织ID")
    protected Long userOrgId;
    @ApiModelProperty(value = "真实姓名")
    protected String realName;
    @ApiModelProperty(value = "店铺名称")
    protected String memberShopName;

    @ApiModelProperty(value = "是否有支付密码")
    protected Boolean isHasPayPassword;

    @ApiModelProperty(value = "登录IP")
    protected String loginIp;

    @ApiModelProperty(value = "审核模式")
    protected Integer checkMode;

    @ApiModelProperty(value = "登录设备ID")
    protected String loginDevice;

    @ApiModelProperty(value = "登录系统平台")
    protected Integer loginPlatform;

    @ApiModelProperty(value = "用户登录地区")
    protected Integer loginRegion;

    @ApiModelProperty(value = "数据权限")
    protected Long dataAuth;

    @ApiModelProperty(value = "业务员ID")
    protected Long salemansId;

}
