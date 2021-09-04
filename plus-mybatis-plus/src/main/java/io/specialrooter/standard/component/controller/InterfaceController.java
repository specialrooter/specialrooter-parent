package io.specialrooter.standard.component.controller;

import io.specialrooter.standard.component.model.InterfaceVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 获取本工程所有接口清单，主要目的获取中文注解
 *
 *@ClassName InterfaceServiceImpl
 *@Author tian_ye
 *@Date 2020/11/19 14:07
 */
@RestController
@RequestMapping("/iif")
@Slf4j
public class InterfaceController {


    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Autowired
    private ApplicationContext context;

    /**
     *  获取url与类和方法的对应信息
     */
    @ApiOperation(value = "获取接口列表", notes = "获取url与类和方法的对应信息")
    @PostMapping("/ult")
    public List<InterfaceVO> getUrlList() {
        List<InterfaceVO> urlList = new ArrayList<>();

        // 获取当前项目启动类的包名
        Map<String, Object> annotatedBeans = context.getBeansWithAnnotation(SpringBootApplication.class);
        String packageName = annotatedBeans.values().toArray()[0].getClass().getPackageName();

        // 获取所有添加requestMapping注解的方法
        Map<RequestMappingInfo, HandlerMethod> handlerMethodsMap = requestMappingHandlerMapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> item : handlerMethodsMap.entrySet()) {
            InterfaceVO interfaceVO = new InterfaceVO();
            RequestMappingInfo info = item.getKey();
            HandlerMethod method = item.getValue();
            PatternsRequestCondition p = info.getPatternsCondition();
            for (String url : p.getPatterns()) {
                // 路径
                interfaceVO.setUrl(url);
            }

            String name = method.getMethod().getDeclaringClass().getName();
            if (StringUtils.isNotBlank(packageName) && !name.contains(packageName)) {
                continue;
            }
            // 类名
            interfaceVO.setClassName(name);

            ApiOperation apiOperation = method.getMethodAnnotation(ApiOperation.class);
            if(apiOperation != null) {
                // 接口说明
                interfaceVO.setApiOperationValue(apiOperation.value());
                // 接口发布说明
                interfaceVO.setApiOperationNotes(apiOperation.notes());
            }

            RequestMethodsRequestCondition methodsCondition = info.getMethodsCondition();
            String type = methodsCondition.toString();
            if (type.startsWith("[") && type.endsWith("]")) {
                type = type.substring(1, type.length() - 1);
                // 方法请求类型
                interfaceVO.setType(type);
            }
            urlList.add(interfaceVO);
        }
        log.info("ServletContext InterfaceList: {}", urlList);
        return urlList;
    }

}
