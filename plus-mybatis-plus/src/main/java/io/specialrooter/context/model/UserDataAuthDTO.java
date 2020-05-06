package io.specialrooter.context.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 当前用户数据权限对象
 * </p>
 *
 * @author xinya.hou
 * @since 2019-07-16
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "当前用户数据权限对象", description = "当前用户数据权限对象")
public class UserDataAuthDTO {


    @ApiModelProperty(value = "用户ids")
    protected List<Long> userIds;

    @ApiModelProperty(value = "运营中心ids")
    protected List<Long> orgIds;

    @ApiModelProperty(value = "店铺ids")
    protected List<Long> storeIds;
}
