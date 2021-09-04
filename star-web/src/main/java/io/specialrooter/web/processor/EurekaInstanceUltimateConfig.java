package io.specialrooter.web.processor;

import io.specialrooter.web.exec.LocalGitExecutor;
import org.springframework.cloud.commons.util.IdUtils;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.cloud.netflix.eureka.metadata.ManagementMetadata;
import org.springframework.cloud.netflix.eureka.metadata.ManagementMetadataProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Map;

//@Configuration
public class EurekaInstanceUltimateConfig {
    @Resource
    private ConfigurableEnvironment env;

    private String getProperty(String property) {
        return this.env.containsProperty(property) ? this.env.getProperty(property) : "";
    }

    @Bean
    public EurekaInstanceConfigBean eurekaInstanceConfigBean(InetUtils inetUtils, ManagementMetadataProvider managementMetadataProvider) {
        String hostname = this.getProperty("eureka.instance.hostname");
        boolean preferIpAddress = Boolean.parseBoolean(this.getProperty("eureka.instance.prefer-ip-address"));
        String ipAddress = this.getProperty("eureka.instance.ip-address");
        boolean isSecurePortEnabled = Boolean.parseBoolean(this.getProperty("eureka.instance.secure-port-enabled"));
        String serverContextPath = this.env.getProperty("server.servlet.context-path", "/");
        int serverPort = Integer.parseInt(this.env.getProperty("server.port", this.env.getProperty("port", "8080")));
        Integer managementPort = (Integer) this.env.getProperty("management.server.port", Integer.class);
        String managementContextPath = this.env.getProperty("management.server.servlet.context-path");
        Integer jmxPort = (Integer) this.env.getProperty("com.sun.management.jmxremote.port", Integer.class);
        EurekaInstanceConfigBean instance = new EurekaInstanceConfigBean(inetUtils);
        instance.setNonSecurePort(serverPort);

        // 扩展开发者信息
        instance.setInstanceId(IdUtils.getDefaultInstanceId(this.env));

        instance.setPreferIpAddress(preferIpAddress);
        instance.setSecurePortEnabled(isSecurePortEnabled);
        if (StringUtils.hasText(ipAddress)) {
            instance.setIpAddress(ipAddress);
        }

        if (isSecurePortEnabled) {
            instance.setSecurePort(serverPort);
        }

        if (StringUtils.hasText(hostname)) {
            instance.setHostname(hostname);
        }

        String statusPageUrlPath = this.getProperty("eureka.instance.status-page-url-path");
        String healthCheckUrlPath = this.getProperty("eureka.instance.health-check-url-path");
        if (StringUtils.hasText(statusPageUrlPath)) {
            instance.setStatusPageUrlPath(statusPageUrlPath);
        }

        if (StringUtils.hasText(healthCheckUrlPath)) {
            instance.setHealthCheckUrlPath(healthCheckUrlPath);
        }

        ManagementMetadata metadata = managementMetadataProvider.get(instance, serverPort, serverContextPath, managementContextPath, managementPort);
        if (metadata != null) {
            instance.setStatusPageUrl(metadata.getStatusPageUrl());
            instance.setHealthCheckUrl(metadata.getHealthCheckUrl());
            if (instance.isSecurePortEnabled()) {
                instance.setSecureHealthCheckUrl(metadata.getSecureHealthCheckUrl());
            }

            Map<String, String> metadataMap = instance.getMetadataMap();
            metadataMap.computeIfAbsent("management.port", (k) -> {
                return String.valueOf(metadata.getManagementPort());
            });

            // 扩充开发者信息
            String name = LocalGitExecutor.name();
            String email = LocalGitExecutor.email();
            if (!StringUtils.isEmpty(name)) {
                metadataMap.putIfAbsent("开发者", name);
            }

            if (!StringUtils.isEmpty(email)) {
                metadataMap.putIfAbsent("Git账号", email);
            }

        } else if (StringUtils.hasText(managementContextPath)) {
            instance.setHealthCheckUrlPath(managementContextPath + instance.getHealthCheckUrlPath());
            instance.setStatusPageUrlPath(managementContextPath + instance.getStatusPageUrlPath());
        }

        this.setupJmxPort(instance, jmxPort);
        return instance;
    }

    private void setupJmxPort(EurekaInstanceConfigBean instance, Integer jmxPort) {
        Map<String, String> metadataMap = instance.getMetadataMap();
        if (metadataMap.get("jmx.port") == null && jmxPort != null) {
            metadataMap.put("jmx.port", String.valueOf(jmxPort));
        }
    }
}
