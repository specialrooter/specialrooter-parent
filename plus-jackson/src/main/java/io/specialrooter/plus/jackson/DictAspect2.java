//package io.specialrooter.plus.jackson;
//
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import com.fasterxml.jackson.databind.JavaType;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.node.ObjectNode;
//import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
//import com.fasterxml.jackson.databind.type.TypeFactory;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
//import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
//import io.specialrooter.message.MessageResponse;
//import org.apache.commons.lang3.StringUtils;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.annotation.Order;
//
//import java.io.IOException;
//import java.lang.reflect.*;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//
///**
// * @author Ai
// */
////@Aspect
////@Order(6)
//public class DictAspect2 {
//    @Autowired
//    private DictHelper dictHelper;
//
//    @Value("${spring.jackson.date-format:yyyy-MM-dd HH:mm:ss}")
//    private String pattern;
//
//    private static Logger log = LoggerFactory.getLogger(DictAspect2.class);
//
////    @Pointcut("@annotation(DictTranslation)")
//    public void dictFiltersPointCut() {
//    }
//
//    private Map<Class, List<DictM>> dictTranslation = new HashMap<>();
//
//
//    public Field[] getAllField(Class clazz) {
//        //Class clazz = object.getClass();
//        List<Field> fieldList = new ArrayList<>();
//        while (clazz != null) {
//            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
//            clazz = clazz.getSuperclass();
//        }
//        Field[] fields = new Field[fieldList.size()];
//        fieldList.toArray(fields);
//        return fields;
//    }
//
//    public void getAllFieldDict(Class clazz) {
//        if (dictTranslation.get(clazz) != null) {
//            return;
//        }
//        List<DictM> dictMList = new ArrayList<>();
//        Field[] declaredFields = getAllField(clazz);
//        for (Field declaredField : declaredFields) {
//            Dict dictAnnotation = declaredField.getAnnotation(Dict.class);
//            if (dictAnnotation != null) {
//                DictM dictM = DictM.builder().field(declaredField.getName()).leaf(true).build();
//                dictM.setDict(dictAnnotation.value());
//                dictM.setRes(dictAnnotation.res());
//                if (StringUtils.isEmpty(dictAnnotation.mapper())) {
//                    dictM.setMapper(dictAnnotation.mapper());
//                }
//                dictMList.add(dictM);
//            }
//        }
//        dictTranslation.put(clazz, dictMList);
//    }
//
//    public static JavaType getJavaType(Type type) {
//        //判断是否带有泛型
//        if (type instanceof ParameterizedType) {
//            Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
//            //获取泛型类型
//            Class rowClass = (Class) ((ParameterizedType) type).getRawType();
//            JavaType[] javaTypes = new JavaType[actualTypeArguments.length];
//            for (int i = 0; i < actualTypeArguments.length; i++) {
//                //泛型也可能带有泛型，递归获取
//                javaTypes[i] = getJavaType(actualTypeArguments[i]);
//            }
//            return TypeFactory.defaultInstance().constructParametricType(rowClass, javaTypes);
//        } else {
//            //简单类型直接用该类构建JavaType
//            Class cla = (Class) type;
//            return TypeFactory.defaultInstance().constructParametricType(cla, new JavaType[0]);
//        }
//    }
//
//    public Class getGenericityClass(JavaType javaType) {
//        List<JavaType> typeParameters = javaType.getBindings().getTypeParameters();
//        for (JavaType typeParameter : typeParameters) {
//            if (typeParameter.getBindings().size() > 0) {
//                return getGenericityClass(typeParameter);
//            } else {
//                return typeParameter.getRawClass();
//            }
//        }
//        return null;
//    }
//
//    public Class setDictTranslationType(Type type) {
//        JavaType javaType = getJavaType(type);
//        Class genericityClass = getGenericityClass(javaType);
//        getAllFieldDict(genericityClass);
//        return genericityClass;
//    }
//
//    public void setDictTranslation(Object object) throws InvocationTargetException, IllegalAccessException {
//        if (object instanceof MessageResponse) {
//            MessageResponse msg = (MessageResponse) object;
//            if (msg.getSuccess()) {
//                Object data = msg.getData();
//                if (data instanceof Page) {
//                    List records = ((Page) data).getRecords();
//                    for (Object record : records) {
//                        List<DictM> dictMList = dictTranslation.get(record.getClass());
//                        if (dictMList.size() > 0) {
//                            for (DictM dictM : dictMList) {
//                                Map<String, String> dict = dictHelper.getDict(dictM.getDict());
//
//                                Object invoke = BeanUtils.getPropertyDescriptor(record.getClass(), dictM.getField()).getReadMethod().invoke(record);
//
//                                if (dictM.isLeaf()) {
//                                    String s = dict.get(String.valueOf(invoke));
//
//                                    if (StringUtils.isNotEmpty(s)) {
//                                        if (StringUtils.isNotEmpty(dictM.getMapper())) {
//                                            BeanUtils.getPropertyDescriptor(record.getClass(), dictM.getMapper()).getWriteMethod().invoke(record, s);
//                                        } else {
//                                            BeanUtils.getPropertyDescriptor(record.getClass(), dictM.getField()).getWriteMethod().invoke(record, s);
//                                        }
//                                    }
//                                }
//                            }
//                        }
//
//                    }
//                }
//
//
//            }
//        }
//    }
//
//    @Around(value = "dictFiltersPointCut()")
//    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
//        long start = System.currentTimeMillis();
//        //后置通知
//        Object result = joinPoint.proceed();
//
//        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
//        Method method = methodSignature.getMethod();
//
//        Class aClass = setDictTranslationType(method.getGenericReturnType());
////        setDictTranslation(result);
////        if(1==1){
////            return result;
////        }
////        Parameter[] parameters = method.getParameters();
////        DictFilter dictFilter = method.getAnnotation(DictFilter.class);
////        DictFilter[] dictFilterArray = null;
////        DictFilters dictFilters = null;
////
////
////
////        if (dictFilter != null) {
////            dictFilterArray = new DictFilter[1];
////            dictFilterArray[0] = dictFilter;
////        } else if ((dictFilters = method.getAnnotation(DictFilters.class)) != null) {
////            dictFilterArray = dictFilters.value();
////        }
//
//        /*if(result!=null){
//            return result;
//        }
//*/
//
////        List<DictFilter> dictResponseFilters = null;
////        if (dictFilterArray != null) {
////            dictResponseFilters = Arrays.asList(dictFilterArray);
//        //处理字典项
//        result = jacksonResponseDictFilterHandler(joinPoint, aClass, result);
////        }
//        System.out.println("DictFilter AOP处理完成：" + (System.currentTimeMillis() - start));
//        return result;
//    }
//
//    private List<String> getNodePath(Object object, String startPath, List<String> paths, Class target) throws IllegalAccessException {
//
//        if (object != null)
//            for (Field declaredField : object.getClass().getDeclaredFields()) {
//                //范型因为实例化之前，T是Object ，需获取一下实际对象
//                if (declaredField.getType().equals(Object.class)) {
//                    declaredField.setAccessible(true);
//                    Object o = declaredField.get(object);
//                    if (o.getClass().equals(target)) {
//                        paths.add(startPath + "/" + declaredField.getName());
//                    } else if (o.getClass().equals(Page.class)) {
//                        paths.add(startPath + "/records");
//                    } else {
//                        //递归寻找
//                        getNodePath(o, "/" + declaredField.getName(), paths, target);
//                    }
//                }
//
//                //普通对象
//                if (declaredField.getType().equals(target)) {
//                    paths.add(startPath + "/" + declaredField.getName());
//                }
//            }
//        return null;
//    }
//
//    /**
//     * 支持返回参数为List<Object>、List<Map>、Object
//     * <p>
//     * //     * @param dictResponseFilters
//     *
//     * @param result
//     * @throws IOException
//     */
//    private Object jacksonResponseDictFilterHandler(JoinPoint joinPoint, Class clazz/*, List<DictFilter> dictResponseFilters*/, Object result) throws IOException, IllegalAccessException {
//        Map<Object, Map> dictFilterMap = new HashMap<>();
//        Map<Object, Dict.Result> dictFilterTypeMap = new HashMap<>();
//
//
//        List<DictM> dictMS = dictTranslation.get(clazz);
//        //获取所有字典项
//        dictMS.forEach(x -> {
//            //System.out.println(x.dict()+"|"+x.value());
//            Map<String, ObjectNode> dicts = dictHelper.getDictForObjectNode(x.getDict(), null, null);
//            //Map<String, Object> dicts = elasticsearchTemplate.dicts(x.dict(), x.id(), x.text(), 0);
//            if (dicts != null) {
//                dictFilterMap.put(x.getField(), dicts);
//                dictFilterTypeMap.put(x.getField(), x.res);
//            }
//        });
//        List<String> paths = new ArrayList<>();
//        // 找到所有需要翻译的对象变成JSONObject.at 可识别路径
//        getNodePath(result, "/data", paths, clazz);
//        System.err.println("可识别路径：");
//        if (paths != null)
//            for (String s : paths) {
//                System.out.println(s);
//            }
//
//        // 查找数据模型：MessageResponse
//        if (result instanceof MessageResponse) {
//            ObjectMapper objectMapper = new ObjectMapper();
//
//            //处理时间格式化
//            JavaTimeModule javaTimeModule = new JavaTimeModule();
//            javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(pattern)));
//            javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(pattern)));
//            javaTimeModule.addSerializer(Long.class, ToStringSerializer.instance)
//                    .addSerializer(Long.TYPE, ToStringSerializer.instance);
//            objectMapper.registerModule(javaTimeModule);
//
//
////            System.out.println(objectMapper.writeValueAsString(result));
//            JsonNode jsonNode = objectMapper.readTree(objectMapper.writeValueAsString(result));
//
////            if(paths.size()>0){
////                for (String path : paths) {
////
////                }
////            }
//
//            //Mybatis Plus page data analysis
//            JsonNode path = paths.size()>0?jsonNode.at("/data/records"):jsonNode.at("/data/records");
//            JsonNode pathObject = jsonNode.at("/data");
////            System.out.println(path.isMissingNode());
//            if (path != null && path.isArray()) {
//                Iterator<JsonNode> it = path.iterator();
//                while (it.hasNext()) {
//                    setDictData((ObjectNode) it.next(), dictFilterMap, dictFilterTypeMap);
//                    /*ObjectNode next = (ObjectNode) it.next();
//                    for (Map.Entry<String, Map> dm : dictFilterMap.entrySet()) {
//                        String s = String.valueOf(next.get(dm.getKey()));
//                        if(StringUtils.isNotEmpty(s)){
//                            ObjectNode o = (ObjectNode) dm.getValue().get(s);
//                            if(o!=null){
//                                if(dictFilterTypeMap.get(dm.getKey()).equals(DictFilter.Result.DDO)){
//                                    next.set("group", o);
//                                    next.remove(dm.getKey());
//                                }else{
//                                    next.put(dm.getKey()+"DDT", o.get("txt").textValue());
//                                }
//                                //next
//                            }
//                        }
//                    }*/
//                }
//            }
//
//            //Save method data analysis
//            if (path.isMissingNode() && pathObject != null && pathObject.isObject()) {
//                setDictData((ObjectNode) pathObject, dictFilterMap, dictFilterTypeMap);
//            }
//
//            // response data list
//            if (pathObject != null && pathObject.isArray()) {
//                Iterator<JsonNode> it = pathObject.iterator();
//                while (it.hasNext()) {
//                    setDictData((ObjectNode) it.next(), dictFilterMap, dictFilterTypeMap);
//                }
//            }
//
//            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
//            return objectMapper.readValue(jsonNode.toString(), methodSignature.getReturnType());
////            System.out.println(result);
////            for (JsonNode node1 : groupIds) {
////            Iterator<JsonNode> it = node1.iterator();
////            while (it.hasNext()) {
////                ObjectNode next = (ObjectNode) it.next();
////                next.set("group", (ObjectNode) dicts.get(next.get("groupId").asText()));
////                next.put("groupText", "xxxx");
////
////            }
////        }
//        }
//        return result;
//
////        List<Map> list = elasticsearchTemplate.list("gwc.gwgroup");
////        ObjectMapper objectMapper = new ObjectMapper();
////        Map<String, Object> dicts = list.stream().collect(Collectors.toMap(map -> String.valueOf(map.get("id")), map -> {
////            ObjectNode node = objectMapper.createObjectNode();
////
////            node.put("id", String.valueOf(map.get("id")));
////            node.put("txt", String.valueOf(map.get("name")));
////            return node;
////        }));
//
//        //JsonNode node = objectMapper.readTree(result);
//
////        List<JsonNode> groupIds = node.findValues("records");
////        for (JsonNode node1 : groupIds) {
////            Iterator<JsonNode> it = node1.iterator();
////            while (it.hasNext()) {
////                ObjectNode next = (ObjectNode) it.next();
////                next.set("group", (ObjectNode) dicts.get(next.get("groupId").asText()));
////                next.put("groupText", "xxxx");
////
////            }
////        }
//    }
//
//    private static ObjectMapper OBJECTMAPPER = new ObjectMapper();
//
//    public void setDictData(ObjectNode node, Map<Object, Map> dictFilterMap, Map<Object, Dict.Result> dictFilterTypeMap) {
//        for (Map.Entry<Object, Map> dm : dictFilterMap.entrySet()) {
//            JsonNode jsonNode = node.get(String.valueOf(dm.getKey()));
//            String s = jsonNode == null ? null : jsonNode.asText();
//            Dict.Result result = dictFilterTypeMap.get(dm.getKey());
//            if (StringUtils.isNotEmpty(s)) {
//                ObjectNode o = (ObjectNode) dm.getValue().get(s);
//
//                if (o != null) {
//                    if (result.equals(Dict.Result.DDO)) {
////                        node.set("group", o);
////                        node.remove(String.valueOf(dm.getKey()));
//                        node.set(String.valueOf(dm.getKey()), o);
//                    } else if (result.equals(Dict.Result.DDT)) {
//                        node.put(dm.getKey() + "Text", o.get("txt").textValue());
//                    } else if (result.equals(Dict.Result.DDL)) {
//                        node.put(String.valueOf(dm.getKey()), o.get("txt").textValue());
//                    }
//                } else {
//                    ObjectNode o1 = OBJECTMAPPER.createObjectNode();
//                    if (dictFilterTypeMap.get(dm.getKey()).equals(Dict.Result.DDO)) {
//                        o1.put("id", (String) null);
//                        o1.put("txt", (String) null);
//                        node.set(String.valueOf(dm.getKey()), o);
////                        node.set("group", o1);
////                        node.remove(String.valueOf(dm.getKey()));
//                    } else {
//                        node.put(dm.getKey() + "DDT", (String) null);
//                    }
//                }
//            } else {
//                ObjectNode o = OBJECTMAPPER.createObjectNode();
//                if (result.equals(Dict.Result.DDO)) {
//                    o.put("id", (String) null);
//                    o.put("txt", (String) null);
//                    node.set(String.valueOf(dm.getKey()), o);
////                    node.set("group", o);
////                    node.remove(String.valueOf(dm.getKey()));
//                } else if (result.equals(Dict.Result.DDT)) {
//                    node.put(dm.getKey() + "Text", (String) null);
//                } else if (result.equals(Dict.Result.DDL)) {
//                    node.put(String.valueOf(dm.getKey()), (String) null);
//                }
//            }
//        }
//    }
//}
//
