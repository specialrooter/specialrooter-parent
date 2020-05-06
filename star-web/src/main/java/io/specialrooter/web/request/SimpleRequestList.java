package io.specialrooter.web.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@ApiModel("查询参数")
@Data
public class SimpleRequestList /*extends JSONObject*/ {
    @ApiModelProperty(value = "组态查询条件，追加查询条件")
    public Map<String,Object> whereConditions;
    @ApiModelProperty(value = "排序字段")
    protected Map<String,Object> sortKeyModes;
    @ApiModelProperty(value = "脱敏字段")
    protected Map<String,Object> desenKeyVals;
    @ApiModelProperty(value = "多表字段前缀映射",example = "{aid=a.id,bid=bid}",hidden = true)
    protected Map<String,Object> columnPrefixMappers;
    @ApiModelProperty(value = "多表连接动态条件",example = "inner join a.id=b.id and xxx=xx",hidden = true)
    protected Map<String,Object> multipleTableJoins;
}
