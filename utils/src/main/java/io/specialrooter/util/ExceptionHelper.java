package io.specialrooter.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionHelper {

    /**
     * 异常信息转换成String
     * @param e
     * @return
     */
    public static String printStackTrace(Throwable e){
        if (e == null){
            return "";
        }
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }
}
