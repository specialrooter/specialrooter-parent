//package io.specialrooter.standard.component.log;
//
//import com.google.common.base.Throwables;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.logging.LogLevel;
//import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
//import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Component;
//
//import java.net.InetAddress;
//import java.util.Map;
//import java.util.concurrent.CompletableFuture;
//
///**
// * 将有异常的接口所在的类开启INFO级别日志
// */
//@Component
//@Slf4j
//public class PrettyLoggersRedisListener {
//
//    @Value(value = "${spring.application.name:}")
//    private String appName;
//
//    @Autowired
//    RedisTemplate<String, Object> redisTemplate;
//
//
////    @Autowired
////    private StringRedisTemplate stringRedisTemplate;
//
//    public boolean isValidateConnection(){
//        if(redisTemplate.getConnectionFactory() instanceof LettuceConnectionFactory){
////            return ((LettuceConnectionFactory)redisTemplate.getConnectionFactory()).getValidateConnection();
//            LettuceConnectionFactory connectionFactory = (LettuceConnectionFactory) redisTemplate.getConnectionFactory();
//            System.out.println("PONG".equals(connectionFactory.getConnection().ping()));
////            System.out.println(.getValidateConnection());
////            System.out.println(connectionFactory.getValidateConnection());
//            return true;
//        }else if(redisTemplate.getConnectionFactory() instanceof JedisConnectionFactory){
//            // Jedis 没有 getValidateConnection 方法
//            PrettyLoggersCloudHandler.log.warn("Jedis在实现上是直接连接的redis server，如果在多线程环境下是非线程安全的，这个时候只有使用连接池，为每个Jedis实例增加物理连接");
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * 监控接口
//     * @param key
//     * @param classLogLevel
//     */
//    public void monitoring(String host,String url, Map<String, LogLevel> classLogLevel) {
////        redisTemplate.boundListOps("AIC:"+appName+":"+key).set();
//
//        // 异常节点 KEY：AIC:{APP.Name}:{IP}:{URL} --> [{Class,Level}]
//
//        // 服务异常发生详情 KEY：AID:{APP.Name}:{IP}:{URL}:{tradeId} --> {request:{uri,ip,header,body},node:{host,hostName,ip,port}}
//
//        // 异步调用Redis
//        CompletableFuture.runAsync(() -> {
//            try{
//                if(isValidateConnection()){
//                    redisTemplate.opsForHash().putAll("AIC:" + appName + ":" + host+":"+url, classLogLevel);
//                }else{
//                    prettyPrint(key,classLogLevel);
//                }
//            }catch (Exception e){
//                PrettyLoggersCloudHandler.log.warn("Redis -> "+Throwables.getRootCause(e).getMessage());
//                prettyPrint(key,classLogLevel);
//            }
//        });
//
//    }
//
//    public void prettyPrint(String key, Map<String, LogLevel> classLogLevel){
//        PrettyLoggersCloudHandler.log.info("request is "+key);
//        PrettyLoggersCloudHandler.log.info("localhost request error log level change.");
//        classLogLevel.forEach((key2,val)->{
//            PrettyLoggersCloudHandler.log.info("Class ["+key2+ "] log Level to ："+val.name());
//        });
//    }
//
//    /**
//     * 清除监控
//     * @param key
//     */
//    public void cleaning(String key){
//        // 异步调用Redis
//        CompletableFuture.runAsync(() -> {
//            if(isValidateConnection()){
//                Boolean delete = redisTemplate.delete("AIC:" + appName + ":" + key);
//                if(delete){
//                    log.info("服务器日志排查结束，恢复默认状态");
//                }else{
//                    log.info("服务器日志排查结束，因数据已销毁，未恢复默认状态，不影响其他操作");
//                }
//            }else{
//                log.info("仅启用本地排查问题结束，重启服务或问题解决后状态自动消失");
//            }
//        });
//
//
////        stringRedisTemplate.opsForHash().delete(key,"");
//    }
//}
