package io.specialrooter.plus.mybatisplus.x;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import io.specialrooter.context.annotation.SearchDate;
import io.specialrooter.context.annotation.SearchLocalDateTime;
import io.specialrooter.plus.mybatisplus.util.DateUtils;
import io.swagger.annotations.ApiModelProperty;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

/**
 * Date 反序列化：支持前端时间区间查询填充：当前开始时间和结束时间
 */
public class SearchDateDeserializer extends JsonDeserializer<Object> implements ContextualDeserializer {


    // 获取注解字段解析或字段名
    private String value;

    private Class<?> rawClass;
    // 获取Date区间的前后值
    private String fill;

    // 必须要保留无参构造方法
    public SearchDateDeserializer() {
        this("", "", null);
    }

    public SearchDateDeserializer(String value, String fill, Class<?> rawClass) {
        this.value = value;
        this.fill = fill;
        this.rawClass = rawClass;
    }

    @Override
    public Object deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        try {

            if (Objects.equals(rawClass, LocalDate.class)) {
                return DateUtils.parseLocalDate(jsonParser.getText());
            } else if (Objects.equals(rawClass, LocalDateTime.class)) {
                return DateUtils.parseLocalDateTime(jsonParser.getText(), fill);
            } else if (Objects.equals(rawClass, Date.class)) {
                return DateUtils.parseDate(jsonParser.getText(), fill);
            } else {
                throw new RuntimeException(value + "::" + "无法识别数据类型");
            }
        } catch (ParseException e) {
            throw new RuntimeException(value + "::" + e.getMessage());
        }
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext deserializationContext, BeanProperty beanProperty) throws JsonMappingException {
        if (beanProperty != null) {
            Class<?> rawClass = beanProperty.getType().getRawClass();
            if (Objects.equals(rawClass, LocalDate.class)
                    || Objects.equals(rawClass, LocalDateTime.class)
                    || Objects.equals(rawClass, Date.class)
            ) { // 非 Date 类直接跳过
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

                        return new SearchDateDeserializer(apiModelProperty.value(), searchDate.fill().value(), rawClass);
                    } else {
                        return new SearchDateDeserializer(beanProperty.getName(), searchDate.fill().value(), rawClass);
                    }
                }
            }
        }
        return deserializationContext.findNonContextualValueDeserializer(beanProperty.getType());
    }
}
