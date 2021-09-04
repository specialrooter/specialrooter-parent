package io.specialrooter.plus.mybatisplus.x;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.NumberSerializers;
import io.specialrooter.plus.mybatisplus.basic.GlobalConstants;

import java.io.IOException;
import java.util.Objects;

/**
 * 后续将不再支持meta表ID增长模式，暂时先保留
 */
public class LongJsonSerializer extends JsonSerializer<Long> implements ContextualSerializer {

    @Override
    public void serialize(Long aLong, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

        if (GlobalConstants.ID_STRATEGY.equals("snow")) {
            jsonGenerator.writeString(String.valueOf(aLong));
        } else
            jsonGenerator.writeNumber(aLong);
    }


    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        if (beanProperty != null) {
            if (Objects.equals(beanProperty.getType().getRawClass(), Long.class)) { // 非 Long 类直接跳过
//                ApiModelProperty apiModelProperty = beanProperty.getAnnotation(ApiModelProperty.class);
//                if (apiModelProperty == null) {
//                    apiModelProperty = beanProperty.getContextAnnotation(ApiModelProperty.class);
//                }

//                if (apiModelProperty != null) {
//                    return new LongJsonSerializer();
//                } else {
                JsonSerialize jsonSerialize = beanProperty.getAnnotation(JsonSerialize.class);

                // 自动处理Long类型
                if ((jsonSerialize!= null && Objects.equals(jsonSerialize.using(),LongJsonSerializer.class)) || beanProperty.getName().equals("id") || beanProperty.getName().equals("key") || beanProperty.getName().equals("ids") || beanProperty.getName().endsWith("Id") || beanProperty.getName().endsWith("ids")) {
                    return new LongJsonSerializer();
                } else {
                    return new NumberSerializers.LongSerializer(Long.class);
                }
//                }

            }
//            return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
        }
        return new LongJsonSerializer();
//        return serializerProvider.findNullValueSerializer(beanProperty);
    }
}
