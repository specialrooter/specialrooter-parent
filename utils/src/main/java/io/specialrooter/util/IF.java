package io.specialrooter.util;

/**
 * Created by Ai on 2017/6/15.
 */
public class IF {
    public static boolean isNotNull(Object obj){
        if(obj!=null && obj.toString().trim().length()>0){
            return true;
        }else {
            return false;
        }
    }

    public static boolean equals(String source,String target){
        if(source.trim().equals(target.trim())){
            return true;
        }
        return false;
    }

    public static boolean isNull(Object obj){
        return !isNotNull(obj);
    }
}
