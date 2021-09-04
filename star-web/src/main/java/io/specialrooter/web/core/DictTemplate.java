//package io.specialrooter.web.core;
//
//import com.google.common.base.CaseFormat;
//import io.specialrooter.context.SpringContext;
//import io.specialrooter.plus.jackson.DictHelper;
//import io.specialrooter.plus.jackson.DictSentry;
//import io.specialrooter.plus.mybatisplus.service.ServicePlusImpl;
//import org.springframework.beans.BeanUtils;
//
//import java.lang.annotation.Annotation;
//import java.lang.reflect.InvocationTargetException;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.CompletableFuture;
//
//
//public class DictTemplate extends DictHelper {
//    public void initSentry(String... basePackages) {
//        initSentry(false, basePackages);
//    }
//
//    public void initSentry(boolean async, String... basePackages) {
//        if ("MEMORY".equals(dictSource)) {
//            try {
//                if (async) {
//                    CompletableFuture.runAsync(() -> {
//                        try {
//                            initMemorySentry(basePackages);
//                        } catch (InvocationTargetException e) {
//                            e.printStackTrace();
//                        } catch (IllegalAccessException e) {
//                            e.printStackTrace();
//                        }
//                    });
//                } else {
//                    initMemorySentry(basePackages);
//                }
//            } catch (InvocationTargetException e) {
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }
//        } else if ("ES".equals(dictSource)) {
//
//        }
//    }
//
//    public void initMemorySentry(String... basePackages) throws InvocationTargetException, IllegalAccessException {
//        for (String s : basePackages) {
//
//            //换成SpringContext获取自定义注解类
//            Class<? extends Annotation> annotationClass = DictSentry.class;
//            Map<String, Object> beansWithAnnotation = SpringContext.getApplicationContext().getBeansWithAnnotation(annotationClass);
//            Set<Map.Entry<String, Object>> entitySet = beansWithAnnotation.entrySet();
//
//            //Set<Class> classes = AnnotationUtils.getClazzFromAnnotation(s, DictSentry.class);
//            //for (Class aClass : classes) {
//            for (Map.Entry<String, Object> entry : entitySet) {
//                Class aClass = entry.getValue().getClass();
//                String to = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, aClass.getSimpleName());
//                ServicePlusImpl bean = SpringContext.getBean(to + "ServiceImpl");
//                System.out.println("loading dict " + to);
//                DictSentry dictSentry = (DictSentry) aClass.getAnnotation(DictSentry.class);
//                for (Object o : bean.list()) {
//                    Object label = BeanUtils.getPropertyDescriptor(o.getClass(), dictSentry.label()).getReadMethod().invoke(o);
//                    Object value = BeanUtils.getPropertyDescriptor(o.getClass(), dictSentry.value()).getReadMethod().invoke(o);
//                    if (value != null) {
//                        put(aClass.getSimpleName(), Long.valueOf(String.valueOf(value)), String.valueOf(label));
//                    }
//                }
//            }
//        }
//
//    }
//
//    public void initSentry(List<Class> classList) {
//        initSentry(false, classList);
//    }
//
//    public void initSentry(boolean async, List<Class> classList) {
//        if ("MEMORY".equals(dictSource)) {
//            try {
//                if (async) {
//                    CompletableFuture.runAsync(() -> {
//                        try {
//                            initMemorySentry(classList);
//                        } catch (InvocationTargetException e) {
//                            e.printStackTrace();
//                        } catch (IllegalAccessException e) {
//                            e.printStackTrace();
//                        }
//                    });
//                } else {
//                    initMemorySentry(classList);
//                }
//            } catch (InvocationTargetException e) {
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }
//        } else if ("ES".equals(dictSource)) {
//
//        }
//    }
//
//    public void initMemorySentry(List<Class> classList) throws InvocationTargetException, IllegalAccessException {
//        //for (String s : basePackages) {
//
//        //换成SpringContext获取自定义注解类
////        Class<? extends Annotation> annotationClass = DictSentry.class;
////        Map<String, Object> beansWithAnnotation = SpringContext.getApplicationContext().getBeansWithAnnotation(annotationClass);
////        Set<Map.Entry<String, Object>> entitySet = beansWithAnnotation.entrySet();
//
//        //Set<Class> classes = AnnotationUtils.getClazzFromAnnotation(s, DictSentry.class);
//        //for (Class aClass : classes) {
//        for (Class aClass : classList) {
//            //Class aClass = entry.getValue().getClass();
//            String to = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, aClass.getSimpleName());
//            ServicePlusImpl bean = SpringContext.getBean(to + "ServiceImpl");
//            System.out.println("loading dict " + to);
//            DictSentry dictSentry = (DictSentry) aClass.getAnnotation(DictSentry.class);
//            for (Object o : bean.list()) {
//                Object label = BeanUtils.getPropertyDescriptor(o.getClass(), dictSentry.label()).getReadMethod().invoke(o);
//                Object value = BeanUtils.getPropertyDescriptor(o.getClass(), dictSentry.value()).getReadMethod().invoke(o);
//                if (value != null) {
//                    put(aClass.getSimpleName(), Long.valueOf(String.valueOf(value)), String.valueOf(label));
//                }
//            }
//        }
//        //}
//
//    }
//
//}
