package io.specialrooter.context.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.specialrooter.plus.mybatisplus.x.LongJsonDeserializer;
import io.specialrooter.plus.mybatisplus.x.LongJsonSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AuthDTO {
    @ApiModelProperty(value = "用户ID")
    @JsonDeserialize(using = LongJsonDeserializer.class)
    @JsonSerialize(using = LongJsonSerializer.class)
    private Long id;
    @ApiModelProperty(value = "用户Token")
    private String token;
    @ApiModelProperty(value = "用户名")
    private String name;
    @ApiModelProperty(value = "用户邮箱")
    private String email;
    @ApiModelProperty(value = "用户手机号")
    private String phone;
    @ApiModelProperty(value = "用户头像")
    private String avatar;
    @ApiModelProperty(value = "微信平台id")
    private String openId;
    @ApiModelProperty(value = "APP id")
//    @JsonDeserialize(using = LongJsonDeserializer.class)
//    @JsonSerialize(using = LongJsonSerializer.class)
    private String appId;

    @ApiModelProperty(value = "认证状态(0未认证1已提交2已认证)")
    private Integer cert;

    @ApiModelProperty("用户状态(0未激活1已激活)")
    private Integer state;
}
