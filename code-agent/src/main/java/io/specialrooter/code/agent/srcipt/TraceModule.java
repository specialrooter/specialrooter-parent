package io.specialrooter.code.agent.srcipt;

import com.alibaba.fastjson.JSON;
import org.openjdk.btrace.core.BTraceUtils;
import org.openjdk.btrace.core.annotations.*;
import org.openjdk.btrace.core.types.AnyType;

import static org.openjdk.btrace.core.BTraceUtils.println;

@BTrace(unsafe = true)
public class TraceModule {
    @TLS
    static Throwable currentException;

    //监控所有controller方法的输入输出
    @OnMethod(clazz = "/com\\.tianan\\.dslr\\..*Controller/",method = "/.*/",location=@Location(Kind.RETURN))
    public static void printMethodLog(@ProbeClassName String pcn, // 被拦截的类名
                                      @ProbeMethodName String pmn,  //被拦截的方法名
                                      AnyType[] args,  //被拦截的方法的参数值
                                      @Return AnyType result , @Duration long duration){
        println("------------------ start ---------------");
        println("className: " + pcn);
        println("MethodName: " + pmn +" duration:"+ duration/1000000 + "ms");
        println("args: " + JSON.toJSONString(args) );
        println("result: " + JSON.toJSONString(result));
        println("------------------ end ---------------");
        println();
    }
    //监控异常
    @OnMethod(
            clazz="/com\\.tianan\\.dslr\\..*/",
            method="/.*/",
            location=@Location(Kind.ERROR)
    )
    public static void onerror(@TargetInstance Throwable exception) {
        println("------------------ start ---------------");
        // 打印异常堆栈
        BTraceUtils.Threads.jstack(exception);
        println("------------------ end ---------------");
        println();
    }
}
