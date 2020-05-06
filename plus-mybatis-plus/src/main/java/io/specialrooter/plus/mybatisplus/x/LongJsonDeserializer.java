package io.specialrooter.plus.mybatisplus.x;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import io.specialrooter.plus.mybatisplus.basic.Constant;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * 后续将不再支持meta表ID增长模式，暂时先保留
 */
public class LongJsonDeserializer extends JsonDeserializer<Long> {
    @Override
    public Long deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        if(jsonParser!=null&& StringUtils.isNotEmpty(jsonParser.getText())){
            if(Constant.ID_STRATEGY.equals("snow")){
                return Long.valueOf(jsonParser.getText());
            }
            return jsonParser.getLongValue();
        }
        return null;
    }
}
