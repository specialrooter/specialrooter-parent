package io.specialrooter.plus.mybatisplus.handler;


import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.google.common.base.CaseFormat;
import io.specialrooter.context.util.ApiUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.UUID;

public class SupportMetaObjectHandler implements MetaObjectHandler {


//    @Value("${specialrooter.develop:false}")
////    private boolean develop;
    @Autowired
    private IdStrategyGenerator idStrategyGenerator;

    @Override
    public void insertFill(MetaObject metaObject) {
        //String className = metaObject.getOriginalObject().getClass().getSimpleName();
        //String to = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, className);
        Long currentUserId = ApiUtils.getCurrentUserId(0L);

        Object id = this.getFieldValByName("id", metaObject);
        //动态插入值
        if (ObjectUtils.isEmpty(id)) {

            long bd_dict = idStrategyGenerator.nextId(metaObject.getOriginalObject().getClass());
            this.setFieldValByName("id", bd_dict, metaObject);
        }

        Object createUserId = this.getFieldValByName("createUserId", metaObject);
        Object modifyUserId = this.getFieldValByName("modifyUserId", metaObject);
        if (ObjectUtils.isEmpty(createUserId)) {
            this.setFieldValByName("createUserId",currentUserId , metaObject);
        }
        if (ObjectUtils.isEmpty(modifyUserId)) {
            this.setFieldValByName("modifyUserId",currentUserId, metaObject);
        }

        LocalDateTime now = LocalDateTime.now();

        this.setFieldValByName("createTime", now, metaObject);
        this.setFieldValByName("modifyTime", now, metaObject);

    }

   /* public static void main(String[] args) {
        Bean1 bean1 = new Bean1();
        MetaObject metaObject = MetaObject.forObject(bean1, new DefaultObjectFactory(), new DefaultObjectWrapperFactory(), new DefaultReflectorFactory());
        System.out.println(metaObject.getOriginalObject().getClass().getName());
    }*/

    @Override
    public void updateFill(MetaObject metaObject) {
        //动态更新值
//        metaObject.setValue("modifyUserId", ApiUtils.getCurrentUserId());
//        metaObject.setValue("modifyTime", LocalDateTime.now());

        Object createUserId = this.getFieldValByName("modifyUserId", metaObject);
        if (ObjectUtils.isEmpty(createUserId)) {
            this.setFieldValByName("modifyUserId",  ApiUtils.getCurrentUserId(0L) , metaObject);
        }
        this.setFieldValByName("modifyTime", LocalDateTime.now(), metaObject);
    }
}
