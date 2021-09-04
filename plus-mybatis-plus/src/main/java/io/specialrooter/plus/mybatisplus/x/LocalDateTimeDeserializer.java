package io.specialrooter.plus.mybatisplus.x;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import io.specialrooter.context.annotation.DateFill;
import io.specialrooter.context.annotation.JsonFormatLocalDateTime;
import io.specialrooter.context.model.Between;
import io.specialrooter.plus.mybatisplus.util.DataUtils;
import io.specialrooter.plus.mybatisplus.util.DateUtils;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;

public class LocalDateTimeDeserializer extends JsonDeserializer<Object> implements ContextualDeserializer {


    // 获取注解字段解析或字段名
    private String value;
    // 获取Date区间的前后值
    private String fill;

    private Class<?> rawClass;

    // 必须要保留无参构造方法
    public LocalDateTimeDeserializer() {
        this("", "", null);
    }

    public LocalDateTimeDeserializer(String value, String fill, Class<?> rawClass) {
        this.value = value;
        this.fill = fill;
        this.rawClass = rawClass;
    }

    @Override
    public Object deserialize(JsonParser p, DeserializationContext deserText) throws IOException, JsonProcessingException {
        try {
            List<LocalDateTime> result = new ArrayList<>();
            if (p.isExpectedStartArrayToken()) {

                List<String> dateList = new ArrayList<>();
                JsonToken t;
                while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
                    dateList.add(p.getText());
                }
                if (dateList.size() == 1) {
                    // 处理单个时间，支持解析自定义格式化
                    result.add(DateUtils.parseLocalDateTime(dateList.get(0), fill));
                } else if (dateList.size() == 2) {
                    // 区间处理
                    result.add(DateUtils.parseLocalDateTime(dateList.get(0), DateFill.START_TIME.value(),".000"));
                    result.add(DateUtils.parseLocalDateTime(dateList.get(1), DateFill.END_TIME.value(),".999"));
                } else {
                    // 其他多个时间进行补位
                    for (String s : dateList) {
                        result.add(DateUtils.parseLocalDateTime(s, fill));
                    }
                }
            }else if(p.isExpectedStartObjectToken()){
                Map<String,LocalDateTime> stringObjectMap = new HashMap<>();
                JsonToken t;
                while ((t = p.nextToken()) != JsonToken.END_OBJECT) {
                    // 获取KEY
                    String key = p.getText();
                    // 获取Value
                    p.nextToken();
                    String value = p.getText();
                    if(StringUtils.isNotBlank(value)){
                        LocalDateTime localDateTime = null;
                        if(key.equals("start")){
                            localDateTime = DateUtils.parseLocalDateTime(value, DateFill.START_TIME.value(),".000");
                        }
                        if(key.equals("end")){
                            localDateTime = DateUtils.parseLocalDateTime(value, DateFill.END_TIME.value(),".999");
                        }
                        stringObjectMap.put(key,localDateTime);
                    }
                }

                Between<LocalDateTime> between = new Between<>();
                between.setStart(stringObjectMap.get("start"));
                between.setEnd(stringObjectMap.get("end"));
                return between;
            } else {
                result.add(DateUtils.parseLocalDateTime(p.getText(), fill));
            }

            if (Objects.equals(rawClass, List.class)) {
                return result;
            } else if (Objects.equals(rawClass, LocalDateTime[].class)) {
                return result.toArray(new LocalDateTime[]{});
            } else if (Objects.equals(rawClass, LocalDateTime.class)) {
                return result.size() > 0 ? result.get(0) : null;
            }

            return null;
        } catch (Exception e) {
            throw new RuntimeException(value + "::" + e.getMessage());
        }
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext deserializationContext, BeanProperty beanProperty) throws JsonMappingException {
        if (beanProperty != null) {
            // 时间数组
            Class<?> rawClass = beanProperty.getType().getRawClass();
            if (Objects.equals(rawClass, LocalDateTime.class)
                    || Objects.equals(rawClass, LocalDateTime[].class)
                    || (Objects.equals(rawClass, List.class) && Objects.equals(beanProperty.getType().getContentType().getRawClass(), LocalDateTime.class))
                    || Objects.equals(rawClass,Between.class) && Objects.equals(beanProperty.getType().getBindings().getTypeParameters().get(0).getRawClass(),LocalDateTime.class)) { // 非 Date 类直接跳过
                JsonFormatLocalDateTime jsonFormatLocalDateTime = beanProperty.getAnnotation(JsonFormatLocalDateTime.class);
                if (jsonFormatLocalDateTime == null) {
                    jsonFormatLocalDateTime = beanProperty.getContextAnnotation(JsonFormatLocalDateTime.class);
                }

                ApiModelProperty apiModelProperty = beanProperty.getAnnotation(ApiModelProperty.class);
                if (apiModelProperty == null) {
                    apiModelProperty = beanProperty.getContextAnnotation(ApiModelProperty.class);
                }

                String dateFill = "";

                if (jsonFormatLocalDateTime != null) {
                    dateFill = jsonFormatLocalDateTime.fill().value();
                }

                if (apiModelProperty != null) {

                    return new LocalDateTimeDeserializer(apiModelProperty.value(), dateFill, rawClass);
                } else {
                    return new LocalDateTimeDeserializer(beanProperty.getName(), dateFill, rawClass);
                }

            }
        }
        return deserializationContext.findNonContextualValueDeserializer(beanProperty.getType());
    }
}

