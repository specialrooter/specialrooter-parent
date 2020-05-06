package io.specialrooter.web.request;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@ApiModel("分页查询参数")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class SimpleRequestPage extends SimpleRequestList{
    @ApiModelProperty(required=true,value = "第一页",example="1")
    private int pageIndex;
    @ApiModelProperty(required=true,value = "每页显示条数",example="20")
    private int pageSize;
    @ApiModelProperty(value = "内部使用参数",hidden=true)
    public Page getPage(){
        return new Page<>(pageIndex, pageSize);
    }
}
