package io.specialrooter.web.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@ApiModel("动态表分页查询参数")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class DynamicRequestPage extends RequestPage{
    private String domain;
    private List<String> selects;
}
