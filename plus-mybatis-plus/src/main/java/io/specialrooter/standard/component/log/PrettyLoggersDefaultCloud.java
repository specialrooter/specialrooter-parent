//package io.specialrooter.standard.component.log;
//
//import io.swagger.models.auth.In;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.logging.LogLevel;
//import org.springframework.cloud.commons.util.InetUtils;
//
//import java.net.InetAddress;
//import java.net.UnknownHostException;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * 系统默认实现日志处理
// */
//public class PrettyLoggersDefaultCloud implements PrettyLoggersCloudHandler {
//
//    @Autowired
//    private PrettyLoggers prettyLoggers;
//    @Autowired
//    private PrettyLoggersRedisListener prettyLoggersRedisListener;
//    @Autowired
//    private IPUtils ipUtils;
//
//    @Override
//    public void open(Exception e) {
//
//        // 判断是否需要开启日志等级调整
//        PrettyLoggers.LoggerLevels root = prettyLoggers.loggerLevels("ROOT");
//        if("WARN".equals(root.getConfiguredLevel()) || "ERROR".equals(root.getConfiguredLevel())){
//            // 开启日志等级调整
//            Map<String, LogLevel> interfaceClass = new HashMap<>();
//            // 动态设置日志等级
//            prettyLoggers.configureLogLevel(e.getStackTrace()[0].getClassName(), LogLevel.INFO);
//            interfaceClass.put(e.getStackTrace()[0].getClassName(), LogLevel.INFO);
//
//            System.out.println("设置日志");
//            prettyLoggers.configureLogLevel("io.specialrooter.dec.cloud.eureka.admin.client.CloudLogHandler",LogLevel.INFO);
//            // 在redis记录错误接口以及影响块文件日志等级
//
//            //获取访问IP
//            String ip = ipUtils.requestIP();
//
//            //获取服务IP和端口
//            IPUtils.ServerInfo serverInfo = ipUtils.serverInfo();
//
//
//            prettyLoggersRedisListener.monitoring(url, interfaceClass);
//        }
//    }
//
//    /**
//     * 项目启动的时候，调用这个方法，初始化日志等级，Redis Key匹配规则：项目+IP+*
//     */
//    @Override
//    public void load(String Key) {
//
//    }
//
//    /**
//     * 如果接口正常，则删除Redis对应日志等级KEY
//     */
//    @Override
//    public void close(String key) {
//
//    }
//}
