package io.specialrooter.plus.mybatisplus.x;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import io.specialrooter.context.annotation.SearchLocalDateTime;
import io.specialrooter.plus.mybatisplus.util.DateUtils;
import io.swagger.annotations.ApiModelProperty;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Date 反序列化：支持前端时间区间查询填充：当前开始时间和结束时间
 */
public class LocalDateTimeJsonDeserializer extends JsonDeserializer<LocalDateTime> implements ContextualDeserializer {


    // 获取注解字段解析或字段名
    private String value;
    // 获取Date区间的前后值
    private String fill;

    // 必须要保留无参构造方法
    public LocalDateTimeJsonDeserializer() {
        this("", "");
    }

    public LocalDateTimeJsonDeserializer(String value, String fill) {
        this.value = value;
        this.fill = fill;
    }

    @Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        try {
            return DateUtils.parseLocalDateTime(jsonParser.getText(), fill);
        } catch (ParseException e) {
            throw new RuntimeException(value + "::" + e.getMessage());
        }
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext deserializationContext, BeanProperty beanProperty) throws JsonMappingException {
        if (beanProperty != null) {
            // 单个字段 和 数组
            if (Objects.equals(beanProperty.getType().getRawClass(), LocalDateTime.class)) { // 非 Date 类直接跳过
                SearchLocalDateTime searchLocalDateTime = beanProperty.getAnnotation(SearchLocalDateTime.class);
                if (searchLocalDateTime == null) {
                    searchLocalDateTime = beanProperty.getContextAnnotation(SearchLocalDateTime.class);
                }

                ApiModelProperty apiModelProperty = beanProperty.getAnnotation(ApiModelProperty.class);
                if (apiModelProperty == null) {
                    apiModelProperty = beanProperty.getContextAnnotation(ApiModelProperty.class);
                }

                if (searchLocalDateTime != null) {
                    if (apiModelProperty != null) {
                        return new LocalDateTimeJsonDeserializer(apiModelProperty.value(), searchLocalDateTime.fill().value());
                    } else {
                        return new LocalDateTimeJsonDeserializer(beanProperty.getName(), searchLocalDateTime.fill().value());
                    }
                }
            }
        }
        return deserializationContext.findNonContextualValueDeserializer(beanProperty.getType());
    }
}
