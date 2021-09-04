package io.specialrooter.web.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;

public class FastJsonBootConfig {
    @Bean
    public HttpMessageConverters fastJsonHttpMessageConverters() {
        //1.定义一个convert消息转换对象
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        //2.添加fastJson的序列化配置信息
        FastJsonConfig fastJsonBootConfig = new FastJsonConfig();
        fastJsonBootConfig.setSerializerFeatures(SerializerFeature.PrettyFormat,
                SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteNullListAsEmpty);
        //3.处理中文乱码问题
        List<MediaType> fastMediaTypes = new ArrayList<>();
        fastMediaTypes.add(MediaType.APPLICATION_JSON);
        //4.在convert中添加配置信息
        fastJsonHttpMessageConverter.setSupportedMediaTypes(fastMediaTypes);
        fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonBootConfig);
        return new HttpMessageConverters(fastJsonHttpMessageConverter);

    }

}
