package io.specialrooter.plus.mybatisplus.tenant;

import com.baomidou.mybatisplus.annotation.TableName;
import io.specialrooter.util.GuavaUtils;
import io.specialrooter.util.ZStringUtils;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class TenantScanFilter implements TypeFilter {
    public static Set<String> domains = new HashSet<>();

    @Override
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
        // 获取当前类的注解信息
        AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
        // 获取当前类信息
        ClassMetadata classMetadata = metadataReader.getClassMetadata();
        // 获取当前类的资源（类的路径）
        Resource resource = metadataReader.getResource();

//        String className = classMetadata.getClassName();
//        System.out.println("----" + className);

        if (Objects.equals(classMetadata.getSuperClassName(),"io.specialrooter.plus.mybatisplus.model.SaaSModel")) {
            MergedAnnotation<TableName> tabName = annotationMetadata.getAnnotations().get(TableName.class);
            if(null!=tabName && !"(missing)".equals(tabName.toString())){
                domains.add(tabName.getString("value"));
            }else{
                // 类路径：只保留最后一部分
                String className = classMetadata.getClassName();
                className = className.substring(className.lastIndexOf(".")+1);
                domains.add(GuavaUtils.humpToColumn(className));
            }

            // 永远也不注册多余的Bean对象到Spring，只是借用他的注册机制，用于多租户字段
//            return false;
        }
        return false;
    }
}
