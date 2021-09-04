package io.specialrooter.plus.mybatisplus.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SaaSModel extends StandardModel {

    @NotNull(message = "运营ID不能为空")
    @ApiModelProperty(value = "运营ID")
    private Long operationId;
}
