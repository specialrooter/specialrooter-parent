package io.specialrooter.plus.mybatisplus.x;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.specialrooter.plus.mybatisplus.basic.Constant;

import java.io.IOException;

/**
 * Integer 转 String
 */
public class IntegerJsonSerializer extends JsonSerializer<Integer> {

    @Override
    public void serialize(Integer integer, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeString(String.valueOf(integer));
    }
}
