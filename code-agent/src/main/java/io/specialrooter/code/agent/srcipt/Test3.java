package io.specialrooter.code.agent.srcipt;

import org.openjdk.btrace.core.BTraceUtils;
import org.openjdk.btrace.core.annotations.*;

/**
 * 3.打印到单独的文件，并可以一直tracer
 * ./btrace  -cp .:game-common.jar:game-data-1.0.0-RELEASE.jar -u -v 15659   HelloWorld.java > btrace.log 2>&1 &
 */
@BTrace
public class Test3 {

    //正则监控所有controller 方法耗时
    @OnMethod(
            // 类名也可以使用正则表达式进行匹配
            clazz = "/com\\.tianan\\.dslr\\.base\\.controller\\..*/",
            // 正则表达式需要写在两个斜杠内
            method = "/.*/",
            location =@Location(Kind.RETURN)
    )
    public static void anyRead(@ProbeClassName String pcn, @ProbeMethodName String pmn, @Duration long duration) {
        BTraceUtils.println(pcn+"."+pmn+",cost:" + duration / 1000000 + " ms" );
        BTraceUtils.println();
    }

    //正则监控所有controller 方法内部外部方法调用耗时
//    @OnMethod(
//            // 类名也可以使用正则表达式进行匹配
//            clazz = "/com\\.tianan\\.dslr\\.base\\.controller\\..*/",
//            // 正则表达式需要写在两个斜杠内
//            method = "/.*/",
//            location = @Location(value = Kind.CALL, clazz = "/.*/", method = "/.*/", where = Where.AFTER)
//    )
    public static void anyRead1(@ProbeClassName String pcn, @ProbeMethodName String pmn,@TargetInstance Object instance,@TargetMethodOrField String targetMethodOrField, @Duration long duration) {
        String targetClass;
        if (instance != null) {
            //调用外部方法
            targetClass = "["+BTraceUtils.classOf(instance)
                     + "." + targetMethodOrField+"]";
        } else {
            // 类属性
            targetClass = targetMethodOrField;
        }
        BTraceUtils.println(pcn+"."+pmn+"-->"+targetClass+",cost:" + duration / 1000000 + " ms" );
    }
}
