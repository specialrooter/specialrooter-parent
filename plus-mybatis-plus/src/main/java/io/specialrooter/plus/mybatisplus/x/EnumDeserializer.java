package io.specialrooter.plus.mybatisplus.x;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.json.JsonReadContext;
import com.fasterxml.jackson.databind.*;

import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import io.specialrooter.context.ifc.IEnum;
import io.specialrooter.context.util.EnumUtility;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.beans.BeanUtils;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class EnumDeserializer extends JsonDeserializer<IEnum> implements ContextualDeserializer {
    // 获取注解字段解析或字段名
    private String value;
    private Class rawClass;

    // 必须要保留无参构造方法
    public EnumDeserializer() {
        this("", null);
    }

    public EnumDeserializer(String value, Class<?> rawClass) {
        this.value = value;
        this.rawClass = rawClass;
    }
    /**
     * 反序列化的处理
     */
    @Override
    public IEnum deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonStreamContext parent = jp.getParsingContext();
        Class currentValueClass = parent.getCurrentValue().getClass();
        String currentName = parent.getCurrentName();
        JsonNode node = jp.getCodec().readTree(jp);
        if (parent.inArray()) {
            currentName = ((JsonReadContext) parent).getParent().getCurrentName();
            currentValueClass = BeanUtils.findPropertyType(currentName, parent.getParent().getCurrentValue().getClass());
            return EnumUtility.getEnumValue(rawClass, node.asText());
        }
        Class clazz = BeanUtils.findPropertyType(currentName, currentValueClass);
        IEnum enumValue = EnumUtility.getEnumValue(clazz, node.asText());
        if(enumValue!=null){
            return enumValue;
        }else{
//            throw new RuntimeException(value + "::" +"未找到对应属性" );
            return null;
        }
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext deserializationContext, BeanProperty beanProperty) throws JsonMappingException {
        if(beanProperty!=null){
            Class<?> rawClass = null;
            if(Objects.equals(beanProperty.getType().getRawClass().getInterfaces()[0],IEnum.class)){
                rawClass = beanProperty.getType().getRawClass();
            }

            if(Objects.equals(beanProperty.getType().getRawClass(), List.class) && beanProperty.getType().getContentType().getInterfaces()!=null && beanProperty.getType().getContentType().getInterfaces().size()>0 && Objects.equals(beanProperty.getType().getContentType().getInterfaces().get(0).getRawClass(), IEnum.class)){
                rawClass = beanProperty.getType().getContentType().getRawClass();
            }

            if(rawClass!=null) {
                ApiModelProperty apiModelProperty = beanProperty.getAnnotation(ApiModelProperty.class);
                if (apiModelProperty == null) {
                    apiModelProperty = beanProperty.getContextAnnotation(ApiModelProperty.class);
                }
                if (apiModelProperty != null) {
                    return new EnumDeserializer(apiModelProperty.value(), rawClass);
                } else {
                    return new EnumDeserializer(beanProperty.getName(), rawClass);
                }
            }
        }
        return null;
    }
}
