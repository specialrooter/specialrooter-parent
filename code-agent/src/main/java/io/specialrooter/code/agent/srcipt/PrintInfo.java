package io.specialrooter.code.agent.srcipt;

import org.openjdk.btrace.core.BTraceUtils;
import org.openjdk.btrace.core.annotations.BTrace;

@BTrace
public class PrintInfo {
    static{
        BTraceUtils.println("System Properties:");
        BTraceUtils.printProperties();
        BTraceUtils.println("VM Flags:");
        BTraceUtils.printVmArguments();
        BTraceUtils.println("OS Enviroment:");
        BTraceUtils.printEnv();
        BTraceUtils.exit();
    }
}