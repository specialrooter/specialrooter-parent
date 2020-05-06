package io.specialrooter.plus.mybatisplus.handler;

import com.google.common.base.CaseFormat;
import io.specialrooter.plus.mybatisplus.basic.Constant;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class IdStrategyGenerator {
    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;

    public Long nextId(Class clazz) {
        String to = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, clazz.getSimpleName());
        return nextId(to);
    }

    public Long nextId(String type) {

        return Constant.ID_STRATEGY.equals("snow") ? snowflakeIdGenerator.nextId() : Constant.ID_STRATEGY.equals("meta") ? MetaSequencer.nextID(type) : null;
    }

    public MetaBatchSequenceData batchIds(Class clazz,long num){
        String to = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, clazz.getSimpleName());
        return MetaSequencer.batchIds(to,num);
    }

    public MetaBatchSequenceData batchIds(String type,long num){
        return MetaSequencer.batchIds(type,num);
    }
}
