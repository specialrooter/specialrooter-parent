package io.specialrooter.plus.mybatisplus.x;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import io.specialrooter.context.annotation.SearchDate;
import io.specialrooter.plus.mybatisplus.util.DateUtils;
import io.swagger.annotations.ApiModelProperty;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Objects;

/**
 * Date 反序列化：支持前端时间区间查询填充：当前开始时间和结束时间
 */
public class DateJsonDeserializer extends JsonDeserializer<Date> implements ContextualDeserializer {


    // 获取注解字段解析或字段名
    private String value;
    // 获取Date区间的前后值
    private String fill;

    // 必须要保留无参构造方法
    public DateJsonDeserializer() {
        this("", "");
    }

    public DateJsonDeserializer(String value, String fill) {
        this.value = value;
        this.fill = fill;
    }

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        try {
            Date date = DateUtils.parseDate(jsonParser.getText(), fill);
            return date;
        } catch (ParseException e) {
            throw new RuntimeException(value + "::" + e.getMessage());
        }
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext deserializationContext, BeanProperty beanProperty) throws JsonMappingException {
        if (beanProperty != null) {
            if (Objects.equals(beanProperty.getType().getRawClass(), Date.class)) { // 非 Date 类直接跳过
                SearchDate searchDate = beanProperty.getAnnotation(SearchDate.class);
                if (searchDate == null) {
                    searchDate = beanProperty.getContextAnnotation(SearchDate.class);
                }

                ApiModelProperty apiModelProperty = beanProperty.getAnnotation(ApiModelProperty.class);
                if (apiModelProperty == null) {
                    apiModelProperty = beanProperty.getContextAnnotation(ApiModelProperty.class);
                }

                if (searchDate != null) {
                    if (apiModelProperty != null) {
                        return new DateJsonDeserializer(apiModelProperty.value(), searchDate.fill().value());
                    } else {
                        return new DateJsonDeserializer(beanProperty.getName(), searchDate.fill().value());
                    }
                }
            }
        }
        return deserializationContext.findNonContextualValueDeserializer(beanProperty.getType());
    }
}
