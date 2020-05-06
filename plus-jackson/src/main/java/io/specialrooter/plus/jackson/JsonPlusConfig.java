package io.specialrooter.plus.jackson;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Ai
 */
@Configuration
public class JsonPlusConfig {

    @Bean
    public JsonPlusFilterAspect jsonPlusFilterAspect() {
        return new JsonPlusFilterAspect();
    }

    @Bean
    public DictFilterAspect dictFilterAspect() {
        return new DictFilterAspect();
    }

    @Bean
    public DictAspect dictAspect() {
        return new DictAspect();
    }

    @Bean
    public DictHelper dictHelper() {
        return new DictHelper();
    }

    @Bean
    public DictUtils dictUtils(){
        return new DictUtils();
    }
}
