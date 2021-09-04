package io.specialrooter.plus.mybatisplus.x;

import brave.Span;
import brave.Tracer;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import io.specialrooter.context.annotation.SearchLong;
import io.specialrooter.util.ExceptionHelper;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 后续将不再支持meta表ID增长模式，暂时先保留
 */
@Slf4j
public class LongJsonDeserializer extends JsonDeserializer<Object> implements ContextualDeserializer {
    // 获取注解字段解析或字段名
    private String value;
    // 数据类型
    private Class<?> rawClass;

    @Autowired
    Tracer tracer;

    // 必须要保留无参构造方法
    public LongJsonDeserializer() {
        this("", null);
    }

    public LongJsonDeserializer(String value, Class<?> rawClass) {
        this.value = value;
        this.rawClass = rawClass;
    }

    @Override
    public Object deserialize(JsonParser p, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
//        System.out.println("转换日志：");

        List<String> longList = new ArrayList<>();
        try {
//            List<Long> longList = new ArrayList<>();
//            // 处理数组
//            if (jsonParser.isExpectedStartArrayToken()) {
//                JsonToken t;
//                while ((t = jsonParser.nextToken()) != JsonToken.END_ARRAY) {
//                    longList.add(Long.valueOf(jsonParser.getText()));
//                }
//            } else {
//                longList.add(Long.valueOf(jsonParser.getText()));
//            }
//
//            if (Objects.equals(rawClass, List.class)) {
//                return longList;
//            } else if (Objects.equals(rawClass, Long[].class)) {
//                return longList.toArray(new Long[]{});
//            } else if (Objects.equals(rawClass, Long.class)) {
//                return longList.size() > 0 ? longList.get(0) : null;
//            }
//            return null;

//        if (jsonParser != null && StringUtils.isNotEmpty(jsonParser.getText())) {
//            if (GlobalConstants.ID_STRATEGY.equals("snow")) {
//                try {
//                    return Long.valueOf(jsonParser.getText());
//                } catch (Exception e) {
//                    throw new RuntimeException(value + "::超出长度或不是数字");
//                }
//
//            }
//            return jsonParser.getLongValue();
//        }
            List<Long> result = new ArrayList<>();
            if (p.isExpectedStartArrayToken()) {
//                log.info("传参是数组");


                JsonToken t;
                while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
                    longList.add(p.getText());
                }

                longList.forEach(l -> {
                    result.add(Long.valueOf(l));
                });
                return result;
            } else if (p.isExpectedStartObjectToken()) {
                log.info("传参是对象,暂不处理");
            } else {
                log.info("传参是基础类型");
                String text = p.getText();
                longList.add(text);
//                log.info("文本值：{}",text);
//                log.info(value + "::::" + text);
                if (StringUtils.isNotBlank(text)) {
                    return Long.valueOf(text);
                }
            }
//            log.info("::jsonParser::" + p);
//            if (p != null) {
//                log.info("转换器：{}", JSON.toJSONString(jsonParser));
//                String text = p.getText();
//                log.info("文本值：{}",text);
//                log.info(value + "::::" + text);
//                if (StringUtils.isNotBlank(text)) {
//                    return Long.valueOf(text);
//                } else {
//                    return null;
//                }
//            } else {
//                log.info(value + "^^^jsonParser is null");
//                return null;
//            }

            return null;

        } catch (Exception e) {
//            System.out.println("转换日志："+e.getLocalizedMessage());
//            e.printStackTrace();

            Span span = tracer.newChild(tracer.currentSpan().context()).name("deserializerException").start();
            span.tag("type", "LongDeserializer");
            span.tag("params", JSON.toJSONString(longList));
            span.tag("message", ExceptionHelper.printStackTrace(e));
            span.finish();


            throw new RuntimeException(value + "::::超出长度或不是数字");
        }
    }

//    public static void main(String[] args) {
//        try{
//            System.out.println(1/0);
//        }catch (Exception e){
//            System.out.println(ExceptionUtils.getStackTrace(e));
//        }
//    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext deserializationContext, BeanProperty beanProperty) throws JsonMappingException {
        if (beanProperty != null) {
            Class<?> rawClass = beanProperty.getType().getRawClass();
            if (Objects.equals(rawClass, Long.class)
                    || Objects.equals(rawClass, Long[].class)
                    || (Objects.equals(rawClass, List.class) && Objects.equals(beanProperty.getType().getContentType().getRawClass(), Long.class))) { // 非 Long 类直接跳过
                ApiModelProperty apiModelProperty = beanProperty.getAnnotation(ApiModelProperty.class);
                if (apiModelProperty == null) {
                    apiModelProperty = beanProperty.getContextAnnotation(ApiModelProperty.class);
                }

                SearchLong searchLong = beanProperty.getAnnotation(SearchLong.class);
                if (searchLong == null) {
                    searchLong = beanProperty.getContextAnnotation(SearchLong.class);
                }

                JsonDeserialize jsonDeserialize = beanProperty.getAnnotation(JsonDeserialize.class);
                if (searchLong != null && (jsonDeserialize != null && Objects.equals(jsonDeserialize.using(), LongJsonDeserializer.class))) {
                    if (apiModelProperty != null) {
                        return new LongJsonDeserializer(apiModelProperty.value(), rawClass);
                    } else {
                        return new LongJsonDeserializer(beanProperty.getName(), rawClass);
                    }
                } else {
                    // 自动处理Long类型
                    if (beanProperty.getName().equals("id") || beanProperty.getName().equals("ids") || beanProperty.getName().endsWith("Id") || beanProperty.getName().endsWith("ids")) {
                        if (apiModelProperty != null) {
                            return new LongJsonDeserializer(apiModelProperty.value(), rawClass);
                        } else {
                            return new LongJsonDeserializer(beanProperty.getName(), rawClass);
                        }
                    }
                }
            }
        }

        return new LongJsonDeserializer("", Long.class);
//        return deserializationContext.findNonContextualValueDeserializer(beanProperty.getType());


    }
}
