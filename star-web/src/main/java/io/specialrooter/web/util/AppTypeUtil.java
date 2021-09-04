package io.specialrooter.web.util;

import io.specialrooter.standard.component.exception.GlobalException;
import org.apache.commons.lang3.StringUtils;
import org.sonatype.guice.plexus.config.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class AppTypeUtil {

    @Value("${specialrooter.cloud.business.agent.appId:}")
    private String agentAppId;

    @Value("${specialrooter.cloud.business.operator.appId:}")
    private String operatorAppId;

    @Value("${specialrooter.cloud.business.reseller.appId:}")
    private String resellerAppId;

    @Value("${specialrooter.cloud.business.supplier.appId:}")
    private String supplierAppId;

    @Value("${specialrooter.cloud.business.operation.appId:}")
    private String operationAppId;

    @Autowired
    private HttpServletRequest request;

    public AppTypeEnum getAppType() {
        String appId = request.getHeader("appId");
        return getAppType(appId);
    }


    public AppTypeEnum getAppType(String appId) {
        if(StringUtils.isBlank(appId)){
            throw new GlobalException(513,"appId不能为空");
        }
        if (appId.equals(agentAppId)) {//代理商
            return AppTypeEnum.AGENT;
        } else if (appId.equals(operatorAppId)) {//运营商（平台）
            return AppTypeEnum.OPERATOR;
        } else if (appId.equals(resellerAppId)) {//分销商
            return AppTypeEnum.RESELLER;
        } else if (appId.equals(supplierAppId)) {//供应商
            return AppTypeEnum.SUPPLIER;
        } else if (appId.equals(operationAppId)) {//SaaS运营商
            return AppTypeEnum.OPERATION;
        }
        return AppTypeEnum.OPEN;
    }

    public String getAppId(Integer appType){
        if (appType.equals(2)) {//代理商
            return agentAppId;
        } else if (appType.equals(4)) {//运营商（平台）
            return operatorAppId;
        } else if (appType.equals(1)) {//分销商
            return resellerAppId;
        } else if (appType.equals(3)) {//供应商
            return supplierAppId;
        } else if (appType.equals(5)) {//开放平台
            return supplierAppId;
        } else if (appType.equals(6)) {//SaaS运营商
            return operationAppId;
        }
        return null;
    }
}
