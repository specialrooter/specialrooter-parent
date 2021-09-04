package io.specialrooter.web.config;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * xxl定时任务框架公共配置类
 *
 * @ClassName XxlExecutorConfig
 * @Author tian_ye
 * @Date 2020/7/14 13:18
 */
@Slf4j
@Data
@ConfigurationProperties(prefix = "spring.xxl.job")
//@ConditionalOnClass(XxlJobSpringExecutor.class)
//@ConditionalOnExpression("${spring.xxl.job.enabled:true}")
//@ConditionalOnExpression("'${spring.xxl.job}'.equals('rabbitmq')")
public class XxlExecutorConfig {

    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * 执行器注册中心地址[选填]，为空则关闭自动注册
     */
    private String addresses = "http://172.30.9.30:8080/xxl-job-admin";

    /**
     * 执行器AppName[选填]，为空则关闭自动注册
     */
    private String appName;

    /**
     * 执行器IP[选填]，为空则自动获取
     */
    private String ip;

    /**
     * 执行器端口号[选填]，小于等于0则自动获取
     */
    private Integer port = 9999;

    /**
     * 访问令牌[选填]，非空则进行匹配校验
     */
    private String accessToken;

    /**
     * 执行器日志路径[选填]，为空则使用默认路径
     */
    private String logPath;
    /**
     * 开启开关[选填]，默认为关闭XXL支持
     */
    private boolean enabled=false;

    /**
     * 日志保存天数[选填]，值大于3时生效
     */
    private Integer logRetentionDays = 30;

    @Bean(initMethod = "start", destroyMethod = "destroy")
    /**
     * 当配置存在并为true是才实例化xxl-job
     */
    @ConditionalOnExpression("${spring.xxl.job.enabled:false}")
    public XxlJobSpringExecutor xxlJobExecutor() {
        log.info(">>>>>>>>>>> xxl-job config init..value.");
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(addresses);
        if (StringUtils.isBlank(appName)) {
            appName = applicationName;
        }
        xxlJobSpringExecutor.setAppname(appName);
        if (StringUtils.isNotBlank(ip)) {
            xxlJobSpringExecutor.setIp(ip);
        }
        if (null != port) {
            xxlJobSpringExecutor.setPort(port);
        }
        if (StringUtils.isNotBlank(accessToken)) {
            xxlJobSpringExecutor.setAccessToken(accessToken);
        }
        if (StringUtils.isBlank(logPath)) {
            logPath = "/data/SchedulerLogs/" + applicationName;
        }
        xxlJobSpringExecutor.setLogPath(logPath);
        xxlJobSpringExecutor.setLogRetentionDays(logRetentionDays);
        log.info(">>>>>>>>>>> xxl-job config init success.");
        return xxlJobSpringExecutor;
    }
}