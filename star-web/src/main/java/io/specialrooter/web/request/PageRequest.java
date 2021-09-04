package io.specialrooter.web.request;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.experimental.Accessors;
import org.apache.poi.ss.formula.functions.T;

@ApiModel("分页查询参数")
@Accessors(chain = true)
public class PageRequest<T> extends SortRequest<T> {
    @ApiModelProperty(required = true, value = "第一页", example = "1")
    private int pageIndex=1;
    @ApiModelProperty(required = true, value = "每页显示条数", example = "10")
    private int pageSize=10;

    @ApiModelProperty(value = "内部使用参数", hidden = true)
    public Page getPage() {
        return new Page<>(pageIndex, pageSize);
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
