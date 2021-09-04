package io.specialrooter.web.aspect;

import brave.Span;
import brave.Tracer;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.connector.ResponseFacade;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 链路由增加controller参数传递节点
 */
@Aspect
@Slf4j
public class LogAspect {
    @Autowired
    Tracer tracer;

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)" +
            " || @annotation(org.springframework.web.bind.annotation.GetMapping)" +
            " || @annotation(org.springframework.web.bind.annotation.DeleteMapping)" +
            " || @annotation(org.springframework.web.bind.annotation.PutMapping)" +
            " || @annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void dictFiltersPointCut() {
    }

    /**
     * 环绕通知
     *
     * @param proceedingJoinPoint
     */
    @Around(value = "dictFiltersPointCut()")
    public Object doBefore(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Span parentSpan = tracer.currentSpan();
        Span span = null;
        if(requestAttributes!=null){
            HttpServletRequest request = requestAttributes.getRequest();

            // 获取请求路径
            // String url = request.getRequestURL().toString();

            // 添加头部信息
            Map<String, String> headerMap = new HashMap<String, String>();
            Enumeration<String> enume = request.getHeaderNames();
            while (enume.hasMoreElements()) {
                String key = enume.nextElement();
                String value = request.getHeader(key);
                headerMap.put(key, value);
            }

            if(parentSpan == null){
                span = tracer.newTrace().name("async").start();
            }else{
                span = tracer.newChild(parentSpan.context()).name("request").start();
            }

            if (headerMap.size() > 0) {
                span.tag("request.head", JSONObject.toJSONString(headerMap));
            }

            MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
            String[] parameterNames = methodSignature.getParameterNames();
            //获取用户请求方法的参数并序列化为JSON格式字符串
//            Map<String, Object> body = new HashMap<>();
            Object[] args = proceedingJoinPoint.getArgs();

            Stream<?> stream = ArrayUtils.isEmpty(args) ? Stream.empty() : Arrays.asList(args).stream();
            List<Object> logArgs = stream
                    .filter(arg -> (!(arg instanceof HttpServletRequest) && !(arg instanceof HttpServletResponse)  && !(arg instanceof RequestFacade) && !(arg instanceof ResponseFacade) && !(arg instanceof MultipartFile) && !(arg instanceof MultipartFile[])))
                    .collect(Collectors.toList());

//            if (args != null && args.length > 0) {
//                for (int i = 0; i < args.length; i++) {
//                    Object arg = args[i];
//                    if (!(arg instanceof RequestFacade) && !(arg instanceof ResponseFacade) && !(arg instanceof MultipartFile)) {
//                        body.put(parameterNames[i], arg);
//                    }
//                }
//            }

            if(logArgs!=null && logArgs.size()>0){
                String bodyString = JSON.toJSONString(logArgs);
                span.tag("request.body", bodyString);
            }

            long l1 = System.currentTimeMillis() - start;
            span.tag("request.logTime", l1 + "");

            span.finish();
        }

//        Thread.currentThread().setName(UUID.randomUUID().toString().substring(0, 12));
//        String params = "";
//        if (joinPoint.getArgs() != null && joinPoint.getArgs().length > 0) {
//            String data = JSON.toJSONString(joinPoint.getArgs()[0]);
//            params += data;
//        }

        //打印请求内容
//        String url = request.getRequestURL().toString();
//        log.info("===============请求内容===============");
//        log.info("请求地址:" + url);
//        log.info("请求方式:" + request.getMethod());
//        log.info("请求类方法:" + joinPoint.getSignature());
//        log.info("请求类方法参数:" + bodyString);
//        log.info("===============请求内容===============");
        // 执行
        Object result = proceedingJoinPoint.proceed();
        Span response = null;
        if(parentSpan == null){
            response = tracer.newTrace().name("async-response").start();
        }else{
            response = tracer.newChild(parentSpan.context()).name("response").start();
        }

        // Span response = tracer.newChild(tracer.currentSpan().context()).name("response").start();
        // 如果span可以记录，则开始记录
        //if(span!=null){
            long l2 = System.currentTimeMillis();
            if (result instanceof Flux) {
//                ((Flux<?>) result).subscribe(System.out::println);
                Stream<?> stream = ((Flux<?>) result).toStream();
                Object[] objects = stream.toArray();
                response.tag("response.data", JSONObject.toJSONString(objects));
            } else {
                response.tag("response.data", JSONObject.toJSONString(result));
            }
            long l3 = System.currentTimeMillis() - l2;
            response.tag("response.logTime", l3 + "");

            response.finish();
        //}
        return result;
    }
}
