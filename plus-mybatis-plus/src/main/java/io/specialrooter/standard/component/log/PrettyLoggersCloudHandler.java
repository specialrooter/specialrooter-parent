package io.specialrooter.standard.component.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 接口异常日志云端接管处理
 * Redis：开启日志状态
 * RabbitMQ: 发送日志处理通知
 *
 * RabbitMQ:幂等性处理方案
 * 参考1：https://blog.csdn.net/weixin_31764625/article/details/112090482
 * 参考2：https://www.icode9.com/content-4-958653.html
 */
public interface PrettyLoggersCloudHandler {
    Logger log = LoggerFactory.getLogger(PrettyLoggersCloudHandler.class);

    void open(Exception ex);

    void load(String Key);

    void close(String key);
}
