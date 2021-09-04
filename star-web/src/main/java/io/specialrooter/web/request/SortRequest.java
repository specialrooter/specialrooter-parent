package io.specialrooter.web.request;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import io.specialrooter.util.GuavaUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.reflection.property.PropertyNamer;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
@Slf4j
public class SortRequest<T> extends TableLogic{
    @ApiModelProperty(value = "排序字段(支持多字段，传入数据格式：name.ascend-age.descend)")
    protected String sort;
    @ApiModelProperty(value = "后台排序变量", hidden = true)
    protected List<QueryOrder> orderList = new ArrayList<>();

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    /**
     * 返回排序列表，便于自定义XML SQL处理
     *
     * @return
     */
    public List<Sorted> sorted() {
        List<Sorted> sortedList = new ArrayList<>();
        if (StringUtils.isNotBlank(sort)) {
            String[] split = sort.split("-");
            for (String s : split) {
                String name = s.substring(0, s.lastIndexOf("."));
                String sort = s.substring(s.lastIndexOf("."));
                if (sort.startsWith("asc")) {
                    sortedList.add(new Sorted(name, SortedEnum.ASC));
                } else if (sort.startsWith("desc")) {
                    sortedList.add(new Sorted(name, SortedEnum.DESC));
                }
            }
        }
        return sortedList;
    }

//    public SortRequest orderBy(OrderBy order, Function<T, ?>... columns){
//        for (Function<T, ?> column : columns) {
//            System.out.println(io.specialrooter.plus.mybatisplus.util.LambdaUtils.getName(column));
//        }
//        return this;
//    }

    public SortRequest orderBy(Sort order, SFunction<T, ?>... columns) {
//        for (SFunction<T, ?> column : columns) {
//            SerializedLambda lambda = LambdaUtils.resolve(column);
//            String fieldName = PropertyNamer.methodToProperty(lambda.getImplMethodName());
//            System.out.println(fieldName);
//        }

        orderList.add(new QueryOrder(columns, order));
        return this;
    }

    private String getName(SFunction<T, ?> column) {
        SerializedLambda lambda = LambdaUtils.resolve(column);
        return PropertyNamer.methodToProperty(lambda.getImplMethodName());
    }

    public void sort(Wrapper wrapper) {
        if (wrapper instanceof QueryWrapper) {
            sort((QueryWrapper) wrapper);
        } else if (wrapper instanceof LambdaQueryChainWrapper) {
            sort((LambdaQueryChainWrapper) wrapper);
        }
    }

    /**
     * 查询-组装-排序
     *
     * @param queryWrapper
     */
    public void sort(QueryWrapper queryWrapper) {
        Assert.notNull(queryWrapper, "queryWrapper 不能为空");
        if (StringUtils.isNotBlank(sort)) {
            // 优先用户排序
            List<String> userOrders = new ArrayList<>();

            String[] split = sort.split("-");
            for (String s : split) {
                String name = s.substring(0, s.lastIndexOf("."));
                String sort = s.substring(s.lastIndexOf(".")+1);
                name = GuavaUtils.humpToColumn(name);
                if (sort.startsWith("asc")) {
                    log.debug(name+"->asc");
                    queryWrapper.orderByAsc(name);
                } else if (sort.startsWith("desc")) {
                    log.debug(name+"->desc");
                    queryWrapper.orderByDesc(name);
                }
                userOrders.add(name);
            }

            // 加入默认排序，剔除用户已选择排序字段
            for (QueryOrder queryOrder : orderList) {
                SFunction[] columns = queryOrder.getColumns();
                Sort sort = queryOrder.getSort();
                List<String> validColumns = new ArrayList<>();
                for (SFunction column : columns) {
                    String name = getName(column);
                    if (!userOrders.contains(name)) {
                        validColumns.add(GuavaUtils.humpToColumn(name));
                    }
                }

                if (sort.equals(Sort.ASC)) {
                    log.debug(validColumns+"->asc");
                    queryWrapper.orderByAsc(validColumns);
                }

                if (sort.equals(Sort.DESC)) {
                    log.debug(validColumns+"->desc");
                    queryWrapper.orderByDesc(validColumns);
                }
            }
        } else {
            // 添加默认排序字段
            for (QueryOrder queryOrder : orderList) {
                SFunction[] columns = queryOrder.getColumns();
                Sort sort = queryOrder.getSort();

                if (sort.equals(Sort.ASC)) {
                    log.debug(columns+"->asc");
                    queryWrapper.orderByAsc(columns);
                }

                if (sort.equals(Sort.DESC)) {
                    log.debug(columns+"->desc");
                    queryWrapper.orderByDesc(columns);
                }
            }
        }
    }

    /**
     * 查询-组装-排序
     *
     * @param queryWrapper
     */
    public void sort(LambdaQueryChainWrapper queryWrapper) {
        Assert.notNull(queryWrapper, "queryWrapper 不能为空");
        if (StringUtils.isNotBlank(sort)) {
            // 优先用户排序
            List<String> userOrders = new ArrayList<>();

            String[] split = sort.split("-");
            for (String s : split) {
                String name = s.substring(0, s.lastIndexOf("."));
                String sort = s.substring(s.lastIndexOf(".")+1);
                if (sort.startsWith("asc")) {
                    queryWrapper.orderByAsc(name);
                } else if (sort.startsWith("desc")) {
                    queryWrapper.orderByDesc(name);
                }
                userOrders.add(name);
            }

            // 加入默认排序，剔除用户已选择排序字段
            for (QueryOrder queryOrder : orderList) {
                SFunction[] columns = queryOrder.getColumns();
                Sort sort = queryOrder.getSort();
                List<String> validColumns = new ArrayList<>();
                for (SFunction column : columns) {
                    String name = getName(column);
                    if (!userOrders.contains(name)) {
                        validColumns.add(name);
                    }
                }

                if (sort.equals(Sort.ASC)) {
                    queryWrapper.orderByAsc(validColumns);
                }

                if (sort.equals(Sort.DESC)) {
                    queryWrapper.orderByDesc(validColumns);
                }
            }
        } else {
            // 添加默认排序字段
            for (QueryOrder queryOrder : orderList) {
                SFunction[] columns = queryOrder.getColumns();
                Sort sort = queryOrder.getSort();

                if (sort.equals(Sort.ASC)) {
                    queryWrapper.orderByAsc(columns);
                }

                if (sort.equals(Sort.DESC)) {
                    queryWrapper.orderByDesc(columns);
                }
            }
        }
    }
}


