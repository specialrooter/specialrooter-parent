package io.specialrooter.util;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class M {
    public static String str(Map map, String key){
        if(map!=null){
            Object o = map.get(key);
            if(IF.isNotNull(o)){
                return String.valueOf(o).trim();
            }
        }

       return null;
    }

    /*public static Boolean b(Map map, String key){
            if(map!=null){
                Object o = map.get(key);
                if(IF.isNotNull(o)){
                    return (boolean) o;
                }
            }

        return null;
    }*/

    public static Date date(Map map, String key){
        if(map!=null) {
            Object o = map.get(key);
            if (IF.isNotNull(o)) {
                return (Date) o;
            }
        }
        return null;
    }

    public static Integer integer(Map map, String key){
        if(map!=null){
            Object o = map.get(key);
            if(IF.isNotNull(o)){
                return Integer.valueOf(String.valueOf(o));
            }
        }
        return null;
    }

    public static Double toDouble(Map map, String key){
        if(map!=null) {
            Object o = map.get(key);
            if (IF.isNotNull(o)) {
                return Double.valueOf(String.valueOf(o));
            }
        }
        return null;
    }

    public static BigDecimal bigDecimal(Map map, String key){
        if(map!=null) {
            Object o = map.get(key);
            if (IF.isNotNull(o)) {
                return (BigDecimal) o;
            }
        }
        return null;
    }


    public static Map<String,Object> newMap(String key,Object val){
        Map<String,Object> map = new HashMap<>();
        map.put(key,val);
        return map;
    }

    public static Map<String,Object> newMap(){
        return new HashMap<>();
    }

    public static boolean eq(Map map,String key,Object val){
        Object o = map.get(key);
        if(o.equals(val)){
            return true;
        }

        return false;
    }

    public static int offset(Map m){
        Integer offset = integer(m, "offset");

        if(offset!=null)return offset;
        else {
            Integer page = integer(m, "page");
            if(IF.isNotNull(page)){
                if(page==1){
                    return 0;
                }
                page=page>1?page-1:page;
                int limit = limit(m);
                return page*limit;
            }else return 0;
        }
    }

    public static int limit(Map m){
        Integer pageSize = integer(m, "pageSize");
        if(pageSize!=null){
            return pageSize;
        }else{
            Integer limit = integer(m, "limit");
            if(limit!=null){
                return limit;
            }else{
                return 0;
            }
        }
    }
}
