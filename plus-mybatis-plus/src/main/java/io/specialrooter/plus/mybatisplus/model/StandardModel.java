package io.specialrooter.plus.mybatisplus.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class StandardModel extends BaseModel{

    /**
     * 排序编号
     */
    protected Long sortId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    protected LocalDateTime createTime;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    protected LocalDateTime modifyTime;

    /**
     * 创建人员ID
     */
    @TableField(fill = FieldFill.INSERT)
    protected Long createUserId;

//    /**
//     * 创建人员名称
//     */
//    @TableField(fill = FieldFill.INSERT)
//    protected String createUser;

    /**
     * 修改人员ID
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    protected Long modifyUserId;

//    /**
//     * 修改人员名称
//     */
//    @TableField(fill = FieldFill.INSERT_UPDATE)
//    protected Long modifyUser;

    /**
     * 删除状态
     */
    @ApiModelProperty(value = "删除状态")
    @TableLogic
    protected Boolean stateDeleted;

    /**
     * 停用状态
     */
    @ApiModelProperty(value = "停用状态")
    private Boolean statePaused;

    /**
     * 锁定状态
     */
    @ApiModelProperty(value = "锁定状态")
    private Boolean stateLocked;
}
