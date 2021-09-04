package io.specialrooter.plus.mybatisplus.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 租户继承类
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class TenantIdModel extends StandardModel {
    @ApiModelProperty(value = "租户ID")
    private Long tenantId;
}
