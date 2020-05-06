package io.specialrooter.plus.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.specialrooter.message.MessageResponse;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author Ai
 */
@Aspect
@Order(6)
public class DictFilterAspect {
    @Autowired
    private DictHelper dictHelper;

    @Value("${spring.jackson.date-format:yyyy-MM-dd HH:mm:ss}")
    private String pattern;

    private static Logger log = LoggerFactory.getLogger(DictFilter.class);

    @Pointcut("@annotation(DictFilters) || @annotation(DictFilter)")
    public void dictFiltersPointCut() {
    }

    @Around(value = "dictFiltersPointCut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        //后置通知
        Object result = joinPoint.proceed();

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Parameter[] parameters = method.getParameters();
        DictFilter dictFilter = method.getAnnotation(DictFilter.class);
        DictFilter[] dictFilterArray = null;
        DictFilters dictFilters = null;



        if (dictFilter != null) {
            dictFilterArray = new DictFilter[1];
            dictFilterArray[0] = dictFilter;
        } else if ((dictFilters = method.getAnnotation(DictFilters.class)) != null) {
            dictFilterArray = dictFilters.value();
        }

        /*if(result!=null){
            return result;
        }
*/

        List<DictFilter> dictResponseFilters = null;
        if (dictFilterArray != null) {
            dictResponseFilters = Arrays.asList(dictFilterArray);
            //处理字典项
            result = jacksonResponseDictFilterHandler(joinPoint,dictResponseFilters,result);
        }
        System.out.println("DictFilter AOP处理完成："+(System.currentTimeMillis()-start));
        return result;
    }

    /**
     * 支持返回参数为List<Object>、List<Map>、Object
     *
     * @param dictResponseFilters
     * @param result
     * @throws IOException
     */
    private Object jacksonResponseDictFilterHandler(JoinPoint joinPoint, List<DictFilter> dictResponseFilters, Object result) throws IOException {
        Map<Object,Map> dictFilterMap = new HashMap<>();
        Map<Object, DictFilter.Result> dictFilterTypeMap = new HashMap<>();
        //获取所有字典项
        dictResponseFilters.forEach(x->{
            //System.out.println(x.dict()+"|"+x.value());
            Map<String, ObjectNode> dicts = dictHelper.getDictForObjectNode(x.dict(), x.id(), x.text());
            //Map<String, Object> dicts = elasticsearchTemplate.dicts(x.dict(), x.id(), x.text(), 0);
            if(dicts!=null){
            dictFilterMap.put(x.value(),dicts);
            dictFilterTypeMap.put(x.value(),x.res());
        }
    });

        // 查找数据模型：MessageResponse
        if(result instanceof MessageResponse){
            ObjectMapper objectMapper = new ObjectMapper();

            //处理时间格式化
            JavaTimeModule javaTimeModule = new JavaTimeModule();
            javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(pattern)));
            javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(pattern)));
            javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
//            javaTimeModule.addSerializer(Long.class, ToStringSerializer.instance)
//                    .addSerializer(Long.TYPE, ToStringSerializer.instance);
            objectMapper.registerModule(javaTimeModule);


//            System.out.println(objectMapper.writeValueAsString(result));
            JsonNode jsonNode = objectMapper.readTree(objectMapper.writeValueAsString(result));

            //Mybatis Plus page data analysis
            JsonNode path = jsonNode.at("/data/records");
            JsonNode pathObject = jsonNode.at("/data");
//            System.out.println(path.isMissingNode());
            if(path!=null && path.isArray()){
                Iterator<JsonNode> it = path.iterator();
                while (it.hasNext()){
                    setDictData((ObjectNode) it.next(),dictFilterMap,dictFilterTypeMap);
                    /*ObjectNode next = (ObjectNode) it.next();
                    for (Map.Entry<String, Map> dm : dictFilterMap.entrySet()) {
                        String s = String.valueOf(next.get(dm.getKey()));
                        if(StringUtils.isNotEmpty(s)){
                            ObjectNode o = (ObjectNode) dm.getValue().get(s);
                            if(o!=null){
                                if(dictFilterTypeMap.get(dm.getKey()).equals(DictFilter.Result.DDO)){
                                    next.set("group", o);
                                    next.remove(dm.getKey());
                                }else{
                                    next.put(dm.getKey()+"DDT", o.get("txt").textValue());
                                }
                                //next
                            }
                        }
                    }*/
                }
            }

            //Save method data analysis
            if(path.isMissingNode() && pathObject!=null && pathObject.isObject()){
                setDictData((ObjectNode) pathObject,dictFilterMap,dictFilterTypeMap);
            }




            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            return objectMapper.readValue(jsonNode.toString(), methodSignature.getReturnType());
//            System.out.println(result);
//            for (JsonNode node1 : groupIds) {
//            Iterator<JsonNode> it = node1.iterator();
//            while (it.hasNext()) {
//                ObjectNode next = (ObjectNode) it.next();
//                next.set("group", (ObjectNode) dicts.get(next.get("groupId").asText()));
//                next.put("groupText", "xxxx");
//
//            }
//        }
        }
        return result;

//        List<Map> list = elasticsearchTemplate.list("gwc.gwgroup");
//        ObjectMapper objectMapper = new ObjectMapper();
//        Map<String, Object> dicts = list.stream().collect(Collectors.toMap(map -> String.valueOf(map.get("id")), map -> {
//            ObjectNode node = objectMapper.createObjectNode();
//
//            node.put("id", String.valueOf(map.get("id")));
//            node.put("txt", String.valueOf(map.get("name")));
//            return node;
//        }));

        //JsonNode node = objectMapper.readTree(result);

//        List<JsonNode> groupIds = node.findValues("records");
//        for (JsonNode node1 : groupIds) {
//            Iterator<JsonNode> it = node1.iterator();
//            while (it.hasNext()) {
//                ObjectNode next = (ObjectNode) it.next();
//                next.set("group", (ObjectNode) dicts.get(next.get("groupId").asText()));
//                next.put("groupText", "xxxx");
//
//            }
//        }
    }
    private static ObjectMapper OBJECTMAPPER = new ObjectMapper();
    public void setDictData(ObjectNode node,Map<Object,Map> dictFilterMap,Map<Object, DictFilter.Result> dictFilterTypeMap){
        for (Map.Entry<Object, Map> dm : dictFilterMap.entrySet()) {
            JsonNode jsonNode = node.get(String.valueOf(dm.getKey()));
            String s = jsonNode==null?null:jsonNode.asText();
            if(StringUtils.isNotEmpty(s)){
                ObjectNode o = (ObjectNode) dm.getValue().get(s);
                if(o!=null){
                    if(dictFilterTypeMap.get(dm.getKey()).equals(DictFilter.Result.DDO)){
//                        node.set("group", o);
//                        node.remove(String.valueOf(dm.getKey()));
                        node.set(String.valueOf(dm.getKey()),o);
                    }else{
                        node.put(dm.getKey()+"DDT", o.get("txt").textValue());
                    }
                }else{
                    ObjectNode o1 = OBJECTMAPPER.createObjectNode();
                    if(dictFilterTypeMap.get(dm.getKey()).equals(DictFilter.Result.DDO)){
                        o1.put("id",(String)null);
                        o1.put("txt",(String)null);
                        node.set(String.valueOf(dm.getKey()),o);
//                        node.set("group", o1);
//                        node.remove(String.valueOf(dm.getKey()));
                    }else{
                        node.put(dm.getKey()+"DDT", (String)null);
                    }
                }
            }else{
                ObjectNode o = OBJECTMAPPER.createObjectNode();
                if(dictFilterTypeMap.get(dm.getKey()).equals(DictFilter.Result.DDO)){
                    o.put("id",(String)null);
                    o.put("txt",(String)null);
                    node.set(String.valueOf(dm.getKey()),o);
//                    node.set("group", o);
//                    node.remove(String.valueOf(dm.getKey()));
                }else{
                    node.put(dm.getKey()+"DDT", (String)null);
                }
            }
        }
    }
}

