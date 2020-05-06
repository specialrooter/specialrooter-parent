package io.specialrooter.code.agent.srcipt;

import org.openjdk.btrace.core.BTraceUtils;
import org.openjdk.btrace.core.annotations.*;
import org.openjdk.btrace.core.types.AnyType;

import static org.openjdk.btrace.core.BTraceUtils.println;
import static org.openjdk.btrace.core.BTraceUtils.sizeof;

@BTrace(trusted = true)
public class HelloWorld {
    @OnMethod(clazz = "/com\\.tianan\\.dslr\\..*Controller/",
            method = "/.*/",
            location = @Location(value = Kind.CALL, clazz = "/.*/", method = "/.*/", where = Where.AFTER)
//            location = @Location(Kind.RETURN)
    )
    public static void func(@Self Object self, @TargetInstance Object instance, @ProbeClassName String className
            , @ProbeMethodName String methodName, @TargetMethodOrField String tragetMethodOrField
            , @Duration long duration //计时器
            , @Return AnyType result // 返回数据
                            //, AnyType[] args //方法请求参数，在获取N层外部方法调用时，不能使用该参数
    ) {


//        println(duration);
//        if (duration > 1000000) {  //如果外部调用耗时大于 1ms 则打印出来
//        println("self: " + self);
//        println("instance: " + instance);

        String targetClass = "";

//        println(instance instanceof Class);
        if (instance != null) {
            //调用外部方法
            targetClass = instance
                    .getClass().getSimpleName() + "." + tragetMethodOrField;
        } else {
            // 类属性
            targetClass = tragetMethodOrField;
        }


        println(className + "." + methodName + "." + targetClass + ",cost:" + duration / 1000000 + " ms" + ",size of:" + sizeof(self));
//        }

//        BTraceUtils.print("attribute:");

        //用此方法打印bean的所有属性

//        BTraceUtils.printFields(result);


//        BTraceUtils.print("departmentName:");

        //打印实体类的某个属
//        Field departmentName = BTraceUtils.field("com.my.entities.Department", "departmentName");

//        Object name = BTraceUtils.get(departmentName, result);
//        println(name);
        //打印实体类，注意这里打印出来的是类似于result:com.my.entities.Department@195fb071的结果，即使实体类重写了
        //toString方法，也不会打印，要想打印所有的属性，用BTraceUtils.printFields(result);方法，或者使用强力模式，手动调用String.valueOf方法。
//        println(strcat("result:", str(result)));

        //这一行必须添加，因为由于btrace缓冲区的缘故，最后一行显示不出来，为了不影响查看结果，所以加一行这个
        //保证想看的结果完全显示
//        println("============================");
    }

    /**
     * 第三方包调用
     * 固定目录，匹配JAR
     * btrace -u -cp .:$(find /apps/xxx_service -type f -name "fastjson-*.jar" 9637 Helloworld.java
     * 固定文件：多个用冒号隔开
     * btrace -u -cp .:/Users/ai/Work/App/mvn-repository/com/alibaba/fastjson/1.2.62/fastjson-1.2.62.jar 9637 HelloWorld.java
     *
     * @param pcn
     * @param pmn
     * @param args
     * @param result
     * @param duration
     */
    //监控所有controller方法的输入输出
//    @OnMethod(clazz = "/com\\.tianan\\.dslr\\..*Controller/",method = "/.*/",location=@Location(Kind.RETURN))
    public static void printMethodLog(@ProbeClassName String pcn, // 被拦截的类名
                                      @ProbeMethodName String pmn,  //被拦截的方法名
                                      AnyType[] args,  //被拦截的方法的参数值
                                      @Return AnyType result, @Duration long duration) {
        println("------------------ start [ctrl]---------------");
        println("className: " + pcn);
        println("MethodName: " + pmn + " duration:" + duration / 1000000 + "ms");
//        println("args: " + JSON.toJSONString(args) );
//        println("result: " + JSON.toJSONString(result));
        println("------------------ end ---------------");
        println();
    }

    //监控异常
//    @OnMethod(clazz="/com\\.tianan\\.dslr\\..*/", method="/.*/", location=@Location(Kind.ERROR))
    public static void onerror(@TargetInstance Throwable exception) {
        println("------------------ start [exp]---------------");
        // 打印异常堆栈
        BTraceUtils.Threads.jstack(exception);
        println("------------------ end ---------------");
        println();
    }
}
