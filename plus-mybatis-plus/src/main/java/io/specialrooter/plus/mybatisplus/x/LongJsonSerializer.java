package io.specialrooter.plus.mybatisplus.x;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.specialrooter.plus.mybatisplus.basic.Constant;

import java.io.IOException;
/**
 * 后续将不再支持meta表ID增长模式，暂时先保留
 */
public class LongJsonSerializer extends JsonSerializer<Long> {

    @Override
    public void serialize(Long aLong, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

        if (Constant.ID_STRATEGY.equals("snow"))
            jsonGenerator.writeString(String.valueOf(aLong));
        else
            jsonGenerator.writeNumber(aLong);
    }
}
