package io.specialrooter.context.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DictItemModel {
    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "字典编号")
    private Long dictId;

    @ApiModelProperty(value = "字典KEY")
    private String dictKey;

    @ApiModelProperty(value = "字典名称")
    private String dictName;

    @ApiModelProperty(value = "选项索引")
    private Long dictItemIndex;

    @ApiModelProperty(value = "选项键名")
    private String dictItemKey;

    @ApiModelProperty(value = "选项键值")
    private String dictItemValue;

    @ApiModelProperty(value = "选项数据")
    private String dictItemData;

    @ApiModelProperty(value = "上级索引")
    private Integer dictItemParentIndex;

    @ApiModelProperty(value = "选项图标")
    private String dictItemIcon;

    @ApiModelProperty(value = "生效状态")
    private Boolean dictItemActiveState;

    @ApiModelProperty(value = "排序")
    protected Long sortId;

    public DictItemModel() {
    }

    public DictItemModel(String dictKey, Long dictItemIndex, String dictItemValue) {
        this.dictKey = dictKey;
        this.dictItemIndex = dictItemIndex;
        this.dictItemValue = dictItemValue;
    }
}
