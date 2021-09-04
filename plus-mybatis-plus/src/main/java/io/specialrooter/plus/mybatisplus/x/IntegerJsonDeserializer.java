package io.specialrooter.plus.mybatisplus.x;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * String 转 Integer
 */
public class IntegerJsonDeserializer extends JsonDeserializer<Integer> {
    @Override
    public Integer deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        if (jsonParser != null && StringUtils.isNotEmpty(jsonParser.getText())) {
            return jsonParser.getIntValue();
        }
        return null;
    }
}
