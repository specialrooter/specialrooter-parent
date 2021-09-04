package io.specialrooter.web.util;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import io.specialrooter.context.annotation.Search;
import io.specialrooter.context.annotation.SearchOption;
import io.specialrooter.context.functional.Functional;
import io.specialrooter.context.model.Between;
import io.specialrooter.plus.mybatisplus.converter.DomainConverter;
import io.specialrooter.plus.mybatisplus.util.DataUtils;
import io.specialrooter.util.GuavaUtils;
import io.specialrooter.web.request.PageRequest;
import io.specialrooter.web.request.SortRequest;
import io.specialrooter.web.request.TableLogic;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class QueryWrapperUtils {

    /**
     * 查询list
     *
     * @param service
     * @param object
     * @return
     */
    public static <T> List<T> list(IService service, Object object) {
        return listLogic2(service, object, service.getEntityClass());
    }

    /**
     * 查询list
     *
     * @param service
     * @param object
     * @return
     */
    private static <T> List<T> listLogic2(IService service, Object object, Class<T> po) {
        List<T> list = service.list(queryWrapper(object, false));
        return list;
    }

    /**
     * 查询list ，不带回调函数
     *
     * @param service
     * @param object
     * @return
     */
    public static <T> List<T> list(IService service, Object object, Class<T> vo) {
        List list = listLogic(service, object, vo);
        List<T> convert = DomainConverter.convert(vo, list);
        return convert;
    }

    /**
     * 查询list ，带回调函数
     *
     * @param service
     * @param object
     * @return
     */
    public static <T, D> List<T> list(IService service, Object object, Class<T> vo, Functional.Callback<T> callback) {
        List list = listLogic(service, object, vo);

        if (list != null && list.size() > 0) {
            List<T> convert = DomainConverter.convert(list, vo, callback);
            return convert;
        } else {
            return Collections.emptyList();
        }
    }

//    public static void main(String[] args) {
//        list(null,null,BaseModel.class,baseModel -> {
//            baseModel.getId();
//        });
//    }

    /**
     * 查询list逻辑
     *
     * @param service
     * @param object
     * @param vo
     * @return
     */
    private static List listLogic(IService service, Object object, Class<?> vo) {
        QueryWrapper queryWrapper = queryWrapper(object, false);
        Field[] allFields = DataUtils.getAllFields(vo);
        List<String> collect = Arrays.stream(allFields).filter(f -> {
            TableField annotation = f.getAnnotation(TableField.class);
            if (f.getName().equals("serialVersionUID") || (annotation != null && annotation.exist() == false)) {
                return false;
            }
            return true;
        }).map(p -> GuavaUtils.humpToColumn(p.getName())).collect(Collectors.toList());
        queryWrapper.select(collect.toArray(new String[]{}));
        List list = service.list(queryWrapper);
        return list;
    }


    /**
     * 查询list
     *
     * @param service
     * @param queryWrapper
     * @return
     */
    public static List list(IService service, QueryWrapper queryWrapper) {
        return service.list(queryWrapper);
    }

    public static List list(IService service, LambdaQueryChainWrapper queryWrapper) {
        return service.list(queryWrapper);
    }

    public static IPage page(IService service, Object object) {
        return page(service, object, null, null);
    }

    public static IPage page(IService service, QueryWrapper queryWrapper) {
        return page(service, queryWrapper, null, null);
    }

    public static IPage page(IService service, LambdaQueryChainWrapper queryWrapper) {
        return page(service, queryWrapper, null, null);
    }

    public static <T> IPage<T> page(IService service, Object object, Class<T> vo) {
        return page(service, object, vo, null);
    }

    public static <T> IPage<T> page(IService service, Object object, Class<T> vo, Functional.Callback<T> callback) {
        Page page;
        if (object instanceof PageRequest) {
            page = ((PageRequest) object).getPage();
        } else {
            page = new Page<>(1, 10);
        }
        Wrapper queryWrapper;
        if (object instanceof QueryWrapper) {
            queryWrapper = (QueryWrapper) object;
        } else if (object instanceof LambdaQueryChainWrapper) {
            queryWrapper = (LambdaQueryChainWrapper) object;
        } else {
            queryWrapper = queryWrapper(object, false);
        }

        IPage<T> page1 = service.page(page, queryWrapper);
        if (vo != null) {
            if (callback != null) {
                return page1.convert(o -> DomainConverter.convert(o, vo, callback));
            } else {
                return page1.convert(o -> DomainConverter.convert(o, vo));
            }
        } else {
            return page1;
        }
    }

    public static QueryWrapper queryWrapper(Object object) {
        return (QueryWrapper) queryWrapper(new QueryWrapper<>(), object, true);
    }

    public static QueryWrapper queryWrapper(Object object, boolean logicDelete) {
        return (QueryWrapper) queryWrapper(new QueryWrapper<>(), object, logicDelete);
    }

    public static LambdaQueryChainWrapper queryLambdaChainWrapper(IService service, Object object) {
        return (LambdaQueryChainWrapper) queryWrapper(new LambdaQueryChainWrapper<>(service.getBaseMapper()), object);
    }

    /**
     * 组装查询条件: 支持单表、一对多条件查询
     * 1.eq  ->  enum 类型|数字类型
     * 2.like -> string
     * 3.bt -> date 区间｜any[]
     * 4.gt/lt -> date 区间
     *
     * @param object
     * @return
     */
    private static Wrapper queryWrapper(Wrapper wrapper, Object object) {
        return queryWrapper(wrapper, object, true);
    }

    private static List<String> ex_ = List.of("class", "pageIndex", "pageSize", "page", "sort");

    private static Wrapper queryWrapper(Wrapper wrapper, Object object, boolean logicDelete) {
        if (!ObjectUtils.isEmpty(object)) {
            // 获取对象所有字段
            Map<String, Field> fieldMap = new HashMap<>();
            Class<?> aClass = object.getClass();
            ReflectionUtils.doWithFields(aClass, field -> fieldMap.put(field.getName(), field));

            final BeanWrapper src = PropertyAccessorFactory.forBeanPropertyAccess(object);
            PropertyDescriptor[] pds = src.getPropertyDescriptors();
            for (PropertyDescriptor pd : pds) {
                String name = pd.getName();
                Object srcValue = src.getPropertyValue(name);

                if (!ex_.contains(name)) {
                    Field field = fieldMap.get(name);
                    Class<?> type = field.getType();
                    if (type.isPrimitive()) {
                        log.warn("查询条件必须为包装类型，不能用基础数据类型，请修改属性数据类型：" + object.getClass().getName() + "." + name);
                    } else {
                        if (!ObjectUtils.isEmpty(srcValue)) {
                            Search search = field.getAnnotation(Search.class);
                            SearchOption option = null;
                            boolean hump = true;
                            if (search != null) {
                                String tableAlias = search.tableAlias();
                                String columnAlias = search.columnAlias();
                                hump = search.hump();
                                boolean query = search.query();

                                if (!query) continue;

                                if (StringUtils.isNotBlank(columnAlias)) {
                                    name = columnAlias;

                                }
                                if (StringUtils.isNotBlank(tableAlias)) {
                                    name = tableAlias + "." + name;
                                }

                                if (!SearchOption.AUTO.equals(search.option())) {
                                    option = search.option();
                                }
                            }
                            if (type.isEnum()) {
                                setQuery(wrapper, SearchOption.EQ, name, hump, srcValue);
                            } else if (type.equals(String.class)) {
                                setQuery(wrapper, option != null ? option : SearchOption.CP, name, hump, srcValue);
                            } else if (Objects.equals(type, List.class)) {
//                                setQuery(wrapper, SearchOption.IN, name, srcValue);
                                List objects = (List) srcValue;

                                // 优先NOT IN/IN
                                if (SearchOption.NI.equals(option) || SearchOption.IN.equals(option)) {
                                    setQuery(wrapper, option, name, hump, srcValue);
                                } else {
                                    if (objects.size() == 2 && SearchOption.BT.equals(option)) {
                                        setQuery(wrapper, SearchOption.BT, name, hump, srcValue);
                                    } else {
                                        // 补全未加LIST IN入筛选条件的
                                        setQuery(wrapper, SearchOption.IN, name, hump, srcValue);
                                    }
                                }
                            } else if (type.isArray()) { //[]
                                // 时间类型处理
                                if (Objects.equals(type.getComponentType(), LocalDate.class) || Objects.equals(type.getComponentType(), LocalDateTime.class) || Objects.equals(type.getComponentType(), Date.class)) {
                                    Object[] objects = (Object[]) srcValue;
                                    if (objects.length == 1) {
                                        setQuery(wrapper, option != null ? option : SearchOption.EQ, name, hump, objects[0]);
                                    } else if (objects.length >= 2) {
                                        setQuery(wrapper, SearchOption.BT, name, hump, srcValue);
                                    }

                                } else if (Objects.equals(type.getComponentType(), Integer.class) || Objects.equals(type.getComponentType(), Long.class) || Objects.equals(type.getComponentType(), BigDecimal.class)) {
                                    Object[] objects = (Object[]) srcValue;
                                    if (objects.length == 2 && SearchOption.BT.equals(option)) {
                                        setQuery(wrapper, SearchOption.BT, name, hump, srcValue);
                                    } else {
                                        setQuery(wrapper, SearchOption.IN, name, hump, srcValue);
                                    }
                                }
                            } else if (Objects.equals(type, LocalDate.class) || Objects.equals(type, LocalDateTime.class) || Objects.equals(type, Date.class)) {
                                setQuery(wrapper, SearchOption.EQ, name, hump, srcValue);
                            } else if (Objects.equals(type, Between.class)) {
                                Between between = (Between) srcValue;
                                if (!ObjectUtils.isEmpty(search) && (StringUtils.isNotEmpty(search.start()) || StringUtils.isNotEmpty(search.end()))) {
                                    if (!ObjectUtils.isEmpty(between.getStart()) && !ObjectUtils.isEmpty(between.getEnd())) {

                                        // 格式化LocalDate 转成LocalDateTime

                                        setQuery(wrapper, SearchOption.BTP, search.start() + "," + search.end(), hump, between);
                                    } else if (!ObjectUtils.isEmpty(between.getStart())) {
                                        setQuery(wrapper, SearchOption.GE, search.start(), hump, between.getStart());
                                    } else if (!ObjectUtils.isEmpty(between.getEnd())) {
                                        setQuery(wrapper, SearchOption.LE, search.end(), hump, between.getEnd());
                                    }
                                } else {
                                    if (!ObjectUtils.isEmpty(between.getStart()) && !ObjectUtils.isEmpty(between.getEnd())) {
                                        setQuery(wrapper, SearchOption.BT, name, hump, new Object[]{between.getStart(), between.getEnd()});
                                    } else if (!ObjectUtils.isEmpty(between.getStart())) {
                                        setQuery(wrapper, SearchOption.GE, name, hump, between.getStart());
                                    } else if (!ObjectUtils.isEmpty(between.getEnd())) {
                                        setQuery(wrapper, SearchOption.LE, name, hump, between.getEnd());
                                    }
                                }
                            } else {
                                // 处理其他数据类型
                                setQuery(wrapper, option != null ? option : SearchOption.EQ, name, hump, srcValue);
                            }
                        }
                    }
                }
            }

            // 组装多表逻辑删除
            if (object instanceof TableLogic && logicDelete) {
                List<String> strings = ((TableLogic) object).aliasAll();
                if (strings.size() > 0) {
                    strings.forEach(s -> {
                        setQuery(wrapper, SearchOption.EQ, s + ".stateDeleted", 0);
                    });
                } else {
                    setQuery(wrapper, SearchOption.EQ, "stateDeleted", 0);
                }
            }


            // 组装排序条件
            if (object instanceof SortRequest) {
                ((SortRequest) object).sort(wrapper);
            }
        }

        return wrapper;

    }

    private static void setQuery(Wrapper wrapper, SearchOption op, String name, Object value) {
        setQuery(wrapper, op, name, true, value);
    }

    private static void setQuery(Wrapper wrapper, SearchOption op, String name, boolean hump, Object value) {
        // 命名转换
        if (hump)
            name = GuavaUtils.humpToColumn(name);

        if (wrapper instanceof QueryWrapper) {
            setQuery((QueryWrapper) wrapper, op, name, value);
        } else if (wrapper instanceof LambdaQueryChainWrapper) {
            setQuery((LambdaQueryChainWrapper) wrapper, op, name, value);
        }
    }

    private static void setQuery(LambdaQueryChainWrapper queryWrapper, SearchOption op, String name, Object value) {
        log.debug(op + "->" + name + "->" + value);

        switch (op) {
            case CP:
                queryWrapper.like(name, value);
                break;
            case EQ:
                queryWrapper.eq(name, value);
                break;
            case GE:
                queryWrapper.ge(name, value);
                break;
            case GT:
                queryWrapper.gt(name, value);
                break;
            case LE:
                queryWrapper.le(name, value);
                break;
            case NE:
                queryWrapper.ne(name, value);
                break;
            case LT:
                queryWrapper.lt(name, value);
                break;
            case IN:
                List list = (List) value;
                queryWrapper.in(name, list.toArray());
                break;
            case NI:
                List list2 = (List) value;
                queryWrapper.notIn(name, list2.toArray());
                break;
            case BT:
                Object[] objects = (Object[]) value;
                queryWrapper.between(name, objects[0], objects[1]);
                break;
        }
    }

    private static void setQuery(QueryWrapper<T> queryWrapper, SearchOption op, String name, Object value) {
        log.debug(op + "->" + name + "->" + value);
        switch (op) {
            case CP:
                queryWrapper.like(name, value);
                break;
            case EQ:
                queryWrapper.eq(name, value);
                break;
            case GE:
                queryWrapper.ge(name, value);
                break;
            case GT:
                queryWrapper.gt(name, value);
                break;
            case LE:
                queryWrapper.le(name, value);
                break;
            case NE:
                queryWrapper.ne(name, value);
                break;
            case LT:
                queryWrapper.lt(name, value);
                break;
            case IN:
                List list = (List) value;
                queryWrapper.in(name, list.toArray());
                break;
            case NI:
                List list2 = (List) value;
                queryWrapper.notIn(name, list2.toArray());
                break;
            case BT:
                if (value instanceof List) {
                    List objects = (List) value;
                    queryWrapper.between(name, objects.get(0), objects.get(1));
                } else {
                    Object[] objects = (Object[]) value;
                    queryWrapper.between(name, objects[0], objects[1]);
                }
                break;
            case BTP:
                String[] split = name.split(",");
                Between between = (Between) value;
                queryWrapper.and(wrapper -> wrapper.between(split[0], between.getStart(), between.getEnd()).or().between(split[1], between.getStart(), between.getEnd()));
                break;
        }
    }
}
