package io.specialrooter.plus.jackson;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Ai
 */
@Aspect
@Order(5)
public class JsonPlusFilterAspect {
//
//    @Autowired
//    private ElasticsearchTemplate elasticsearchTemplate;

    private static Logger log = LoggerFactory.getLogger(JsonPlusFilterAspect.class);

    @Pointcut("@annotation(JsonPlusFilters)" + "@annotation(JsonPlusFilter)")
    public void jsonExtensionFiltersPointCut() {
    }


    @Around(value = "jsonExtensionFiltersPointCut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Parameter[] parameters = method.getParameters();
        // 获取注解
        JsonPlusFilter jsonPlusFilterElem = method.getAnnotation(JsonPlusFilter.class);
        JsonPlusFilter[] jsonPlusFilterArray = null;
        JsonPlusFilters jsonPlusFilters = null;
//        DictFilter dictFilter = method.getAnnotation(DictFilter.class);
//        DictFilter[] dictFilterArray = null;
//        DictFilters dictFilters = null;

        if (jsonPlusFilterElem != null) {
            jsonPlusFilterArray = new JsonPlusFilter[1];
            jsonPlusFilterArray[0] = jsonPlusFilterElem;
        } else if ((jsonPlusFilters = method.getAnnotation(JsonPlusFilters.class)) != null) {
            jsonPlusFilterArray = jsonPlusFilters.value();
        }
        List<JsonPlusFilter> requestFilters = new ArrayList<>();
        List<JsonPlusFilter> responseFilters = new ArrayList<>();


//        if (dictFilter != null) {
//            dictFilterArray = new DictFilter[1];
//        } else if ((dictFilters = method.getAnnotation(DictFilters.class)) != null) {
//            dictFilterArray = dictFilters.value();
//        }

        if (jsonPlusFilterArray == null || jsonPlusFilterArray.length <= 0) {
//            if (dictFilterArray == null) {
            return joinPoint.proceed();
//            }
        }

//        List<DictFilter> dictResponseFilters = null;
//        if (dictFilterArray != null) {
//            Arrays.stream(dictFilterArray).collect(Collectors.toList());
//        }

        // 对于含有多个输入VO时，过滤参数要考虑参数的顺序问题，当前只支持一个VO参数
        // 解析出request请求和response请求
        requestFilters = Arrays.stream(jsonPlusFilterArray)
                .filter(jsonPlusFilter -> jsonPlusFilter.type() == JsonPlusFilter.FilterType.REQUEST)
                .collect(Collectors.toList());
        responseFilters = Arrays.stream(jsonPlusFilterArray)
                .filter(jsonPlusFilter -> jsonPlusFilter.type() == JsonPlusFilter.FilterType.RESPONSE)
                .collect(Collectors.toList());
        // 处理request前置通知
        Object[] proprocessArgs = jacksonRequestFilterHandler(joinPoint, requestFilters, parameters);
        // 方法执行
        Object result = (proprocessArgs == null) ? joinPoint.proceed() : joinPoint.proceed(proprocessArgs);
        //处理response之前，设置数据字典
        //jacksonResponseDictFilterHandler(joinPoint, dictResponseFilters, result);

        // 处理response通知
        Object o = jacksonResponseFilterHandler(joinPoint, responseFilters, result);
        System.out.println("JsonPlusFilter AOP处理完成："+(System.currentTimeMillis()-start));
        return o;

    }

    /**
     * 获取request过滤的对象数组，用于传递到下一个通知中去，只支持包含一个REQUEST注解
     *
     * @return Object[] 如果为null,不做前置通知处理
     * @Title: jacksonRequestFilterHandler
     */
    private Object[] jacksonRequestFilterHandler(ProceedingJoinPoint joinPoint, List<JsonPlusFilter> requestFilters,
                                                 Parameter[] parameters) throws IOException {
        // 只支持参数为一个,如果含有多个参数，要解决方法参数列表中，哪个参数的值为要过滤的类型，joinPoint.getArgs(),不能确定参数类型
        Object[] methodArgs = joinPoint.getArgs();
        if (requestFilters == null || requestFilters.size() != 1 || methodArgs == null || methodArgs.length != 1) {
            return null;
        }
        JsonPlusFilter jsonPlusFilter = requestFilters.get(0);
        // 获取解析的要过滤的class
        final Class<?> clazz = jsonPlusFilter.value();
        if (clazz == null) {
            return null;
        }
        // 判断类型参数是否包含此class
        if (parameters == null || parameters.length <= 0
                || Arrays.asList(parameters).stream().noneMatch(parameter -> parameter.getType() == clazz)) {
            return null;
        }
        // 判断对应参数值是否合法，暂时只支持参数列表为一个的情况，对于多个的情况，要过滤的VO必须放在第一个
        // 取第一个作为参数值

        // 判断要include filter和exclude filter过滤
        // 获取注解参数,不包含父类参数(对于继承暂时不考虑,继承类要考虑参数相同的情况，)
        Field[] fields = getClassFields(clazz);
        final String[] includeFields = jsonPlusFilter.include();
        final String[] excludeFields = jsonPlusFilter.exclude();

        // 获取可以进行过滤的属性，優先使用inlucde中屬性，如果inlucde為空，使用exclude屬性,(如果都不为空使用include)
        Set<String> includeFilterFields = getFilterFields(fields, includeFields);
        ;
        Set<String> excludeFilterFields = getFilterFields(fields, excludeFields);
        ;
        if (includeFilterFields == null && excludeFilterFields == null) {
            return null;
        }
        // 处理request参数
        ObjectMapper objectMapper = new ObjectMapper();
        if (includeFilterFields != null) {
            /*objectMapper = */
            getMixObjectMapper(objectMapper, clazz, JsonPlusFilterBean.IncomeParameterIncludeFilter.class, includeFilterFields,
                    JsonPlusFilterBean.INCOME_PARAMETER_INCLUDE_FILTER, true);
        } else if (excludeFilterFields != null) {
            /*objectMapper = */
            getMixObjectMapper(objectMapper, clazz, JsonPlusFilterBean.IncomeParameterExcludeFilter.class, excludeFilterFields,
                    JsonPlusFilterBean.INCOME_PARAMETER_EXCLUDE_FILTER, false);
        } else if (objectMapper == null) {
            return null;
        }
        // 对于多个参数，获取类型clazz，的值，依次更改Object的值
        String argsJson = objectMapper.writeValueAsString(joinPoint.getArgs()[0]);
        Object[] filterArgs = new Object[]{objectMapper.readValue(argsJson, clazz)};
        log.debug("Request method =[{}],parameter=[{}],filterParameter=[{}]", joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs()), argsJson);
        return filterArgs;
    }

    /**
     * 优化支持多个参数
     *
     * @return ProceedingJoinPoint
     * @Title: jacksonResponseFilterHandler
     */
    private Object jacksonResponseFilterHandler(ProceedingJoinPoint joinPoint, List<JsonPlusFilter> responseFilters,
                                                Object result) throws IOException {
        // 处理response参数
        ObjectMapper objectMapper = new ObjectMapper();
        String argsJson = null;
        SimpleFilterProvider prov = null;
        if (responseFilters != null && responseFilters.size() > 0) {
            prov = new SimpleFilterProvider();
            int includeCounter = 0;
            int excludeCounter = 0;
            for (JsonPlusFilter responseFilter : responseFilters) {
                // 只支持参数为一个VO的情况
//                if (responseFilters == null || responseFilters.size() != 1) {
//                    return result;
//                }
                JsonPlusFilter jsonPlusFilter = responseFilter;
                // 获取解析的要过滤的class
                final Class<?> clazz = jsonPlusFilter.value();
                if (clazz == null) {
                    return result;
                }
                // 判断要include filter和exclude filter过滤
                // 获取注解参数,不包含父类参数(对于集成暂时不考虑)
                Field[] fields = getAllFields(clazz);
                final String[] includeFields = jsonPlusFilter.include();
                final String[] excludeFields = jsonPlusFilter.exclude();
                Class<?>[] includeClass = jsonPlusFilter.includeClass();
                Class<?>[] excludeClass = jsonPlusFilter.excludeClass();
                JsonPlusFilter.FilterType type = jsonPlusFilter.type();

                // 获取可以进行过滤的属性，优先使用include中属性，如果include为空，使用exclude属性,(如果都不为空使用include)
                Set<String> includeFilterFields = getFilterFields(fields, includeFields);
                Set<String> excludeFilterFields = getFilterFields(fields, excludeFields);

                //如果配置了includeClass属性，那么进行叠加，并且排除重复项
                if (includeClass != null && includeClass.length > 0) {
                    Set<String> filterFields = getFilterFields(fields, includeClass);
                    if (filterFields != null && filterFields.size() > 0) {
                        if (includeFilterFields == null) {
                            includeFilterFields = new HashSet<>();
                        }
                        includeFilterFields.addAll(filterFields);
                    }

                }

                if (excludeClass != null && excludeClass.length > 0) {
                    Set<String> filterFields = getFilterFields(fields, excludeClass);
                    if (filterFields != null && filterFields.size() > 0) {
                        if (excludeFilterFields == null) {
                            excludeFilterFields = new HashSet<>();
                        }
                        excludeFilterFields.addAll(filterFields);
                    }
                }

                if (includeFilterFields == null && excludeFilterFields == null) {
                    return result;
                }

                if (includeFilterFields != null) {
                    setMixObjectMapper(objectMapper, clazz, JsonPlusFilterBean.outcomeParameterIncludeFilters[includeCounter], prov, includeFilterFields,
                            JsonPlusFilterBean.OUTCOME_PARAMETER_INCLUDE_FILTERS[includeCounter], true);
                    includeCounter++;
//                    objectMapper.addMixIn(clazz, JsonExtensionFilterBean.OutcomeParameterIncludeFilter.class);

//                    objectMapper = getMixObjectMapper(objectMapper,clazz, JsonExtensionFilterBean.OutcomeParameterIncludeFilter.class, includeFilterFields,
//                            JsonExtensionFilterBean.OUTCOME_PARAMETER_INCLUDE_FILTER, true);
                } else if (excludeFilterFields != null) {
                    setMixObjectMapper(objectMapper, clazz, JsonPlusFilterBean.outcomeParameterExcludeFilters[excludeCounter], prov, excludeFilterFields,
                            JsonPlusFilterBean.OUTCOME_PARAMETER_EXCLUDE_FILTERS[excludeCounter], false);
                    excludeCounter++;
//                    objectMapper.addMixIn(clazz, JsonExtensionFilterBean.OutcomeParameterExcludeFilter.class);
//                    objectMapper = getMixObjectMapper(objectMapper,clazz, JsonExtensionFilterBean.OutcomeParameterExcludeFilter.class, excludeFilterFields,
//                            JsonExtensionFilterBean.OUTCOME_PARAMETER_EXCLUDE_FILTER, false);
                } else if (objectMapper == null) {
                    return result;
                }
            }

            //result list to list<map>
            //jdk 11
            /*if(result instanceof MessageResponse){
                MessageResponse msgResponse = (MessageResponse) result;
                Object data = msgResponse.getData();
                if(data instanceof Page){
                    List<Map> list1 = BeanUtils.beansToMaps(((Page) data).getRecords());
                    System.out.println(list1);


                    for (Map map : list1) {
                        map.replace("groupId",*//*dict.get(groupId)*//*132456798);
                    }

                    result = list1;
                }
            }*/


            if (prov != null) {
                argsJson = objectMapper.writer(prov).writeValueAsString(result);
            }
        }

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        return objectMapper.readValue(argsJson, methodSignature.getReturnType());
    }

    private Field[] getClassFields(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        return FieldUtils.getAllFields(clazz);
    }

    public static void jsonLeaf(JsonNode node) {
        if (node.isValueNode()) {

            System.out.println(node.toString());
            return;
        }

        if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> it = node.fields();
            while (it.hasNext()) {
                Map.Entry<String, JsonNode> entry = it.next();
                System.out.println(entry.getKey());
                if (entry.getKey().equals("groupId")) {
                    ObjectNode value = (ObjectNode) entry.getValue();
                    value.put("group", "{xxx:xxx}");
                }
                jsonLeaf(entry.getValue());
            }
        }

        if (node.isArray()) {
            Iterator<JsonNode> it = node.iterator();
            while (it.hasNext()) {
                jsonLeaf(it.next());
            }
        }
    }

    /**
     * 支持查找父节点所有字段，这里暂未使用，仅作技术性代码保留
     *
     * @param clazz
     * @return
     */
    @Deprecated
    private Field[] getAllFields(Class<?> clazz) {
        Class c = clazz;
        List<Field> fieldList = new ArrayList<>();
        while (c != null) {
            fieldList.addAll(new ArrayList<>(Arrays.asList(c.getDeclaredFields())));
            c = c.getSuperclass();
        }
        Field[] fields = new Field[fieldList.size()];
        fieldList.toArray(fields);
        return fields;
    }

    /**
     * @param fields:object
     * @return Set<String>
     * @Title: getFilterFields
     * @includeFileds field must filter
     */
    private Set<String> getFilterFields(Field[] fields, final String[] toBeFilterFileds) {
        if (fields == null || fields.length <= 0 || toBeFilterFileds == null || toBeFilterFileds.length <= 0) {
            return null;
        }
        Set<String> includeFilterFields = Arrays.stream(fields)
                .filter(filed -> Arrays.asList(toBeFilterFileds).contains(filed.getName()))
                .map(filed -> filed.getName()).collect(Collectors.toSet());
        return includeFilterFields;
    }

    private Set<String> getFilterFields(Field[] fields, Class<?>[] clazz) {
        if (fields == null || fields.length <= 0 || clazz == null) {
            return null;
        }
        Set<String> includeFilterFields = Arrays.stream(fields)
                .filter(field -> {
                    JsonView jsonView = field.getAnnotation(JsonView.class);
                    if (jsonView != null) {
                        List<Class<?>> classes = Arrays.asList(jsonView.value());
                        for (Class<?> aClass : clazz) {
                            if (classes.contains(aClass)) {
                                return true;
                            }
                        }
                    }
                    return false;
                })
                .map(filed -> filed.getName()).collect(Collectors.toSet());
        return includeFilterFields;
    }

    private void setMixObjectMapper(ObjectMapper objectMapper, Class<?> toBeMixVo, Class<?> toBeMixFilter, SimpleFilterProvider simpleFilterProvider, Set<String> toBeFilterFields,
                                    String filterId, boolean isInclude) {
        if (isInclude) {
            simpleFilterProvider.addFilter(filterId,
                    SimpleBeanPropertyFilter.filterOutAllExcept(toBeFilterFields));
        } else {
            simpleFilterProvider.addFilter(filterId,
                    SimpleBeanPropertyFilter.serializeAllExcept(toBeFilterFields));
        }
        objectMapper.addMixIn(toBeMixVo, toBeMixFilter);
    }

    @Deprecated
    private void getMixObjectMapper(ObjectMapper objectMapper, Class<?> toBeMixVo, Class<?> toBeMixFilter, Set<String> toBeFilterFields,
                                    String filterId, boolean isInclude) {
        //ObjectMapper objectMapper = new ObjectMapper();
        if (isInclude) {
            objectMapper.setFilterProvider(new SimpleFilterProvider().addFilter(filterId,
                    SimpleBeanPropertyFilter.filterOutAllExcept(toBeFilterFields)));
        } else {
            objectMapper.setFilterProvider(new SimpleFilterProvider().addFilter(filterId,
                    SimpleBeanPropertyFilter.serializeAllExcept(toBeFilterFields)));
        }
        objectMapper.addMixIn(toBeMixVo, toBeMixFilter);
        //return objectMapper;
    }
}

