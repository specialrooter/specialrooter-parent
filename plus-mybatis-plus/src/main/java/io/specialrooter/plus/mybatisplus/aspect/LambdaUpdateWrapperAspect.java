package io.specialrooter.plus.mybatisplus.aspect;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.extension.service.IService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 解决：Server lambdaUpdate().set(...).update(); 不填充更新时间和更新人问题
 */
@Aspect
//@Component
@Slf4j
public class LambdaUpdateWrapperAspect implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    private Map<String,Object> entityMap = new HashMap<>();
    @Pointcut("execution(* com.baomidou.mybatisplus.extension.service.IService.update(com.baomidou.mybatisplus.core.conditions.Wrapper))")
    public void pointcut(){

    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     *重写update(Wrapper<T> updateWrapper), 更新时自动填充不生效问题
     * @param pjp
     * @return
     */
    private Object updateEntity(ProceedingJoinPoint pjp){
        Object[] args = pjp.getArgs();
        if(args != null && args.length == 1){
            Object arg = args[0];
            if(arg instanceof Wrapper){
                Wrapper updateWrapper = (Wrapper)arg;
                Object entity = updateWrapper.getEntity();
                IService service = (IService) applicationContext.getBean(pjp.getTarget().getClass());
                if(ObjectUtils.isEmpty(entity)){
                    entity = entityMap.get(pjp.getTarget().getClass().getName());
                    if(ObjectUtils.isEmpty(entity)){
                        Class entityClz = ReflectionKit.getSuperClassGenericType(pjp.getTarget().getClass(), 1);
                        try {
                            entity = entityClz.newInstance();
                        } catch (InstantiationException e) {
                            log.warn("Entity instantiating exception!");
                        } catch (IllegalAccessException e) {
                            log.warn("Entity illegal access exception!");
                        }
                        entityMap.put(pjp.getTarget().getClass().getName(),entity);
                    }

                }
                return service.update(entity,updateWrapper);
            }
        }

        return null;

    }
}
