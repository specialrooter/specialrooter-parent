//package io.specialrooter.util;
//
//import io.specialrooter.http.HttpRequestUtils;
//import org.apache.http.client.utils.HttpClientUtils;
//
//public class AIHuaweiUtils {
//
//    public static void main(String[] args) throws Exception {
//        String url = "https://ocr.cn-south-1.myhuaweicloud.com/v2/04eca0708f000fc32f34c01071701466/ocr/web-image";
//
//        // X-Auth-Token: token
//        String TOKEN_URL = "https://iam.myhuaweicloud.com/v3/auth/tokens";
//        String jsonString = "{\"auth\":{\"identity\":{\"methods\":[\"password\"],\"password\":{\"user\":{\"domain\":{\"name\":\"xinglian\"},\"name\":\"bo_ai\",\"password\":\"nnc!3\"v$53#HvJY82V\"}}},\"scope\":{\"project\":{\"name\":\"cn-south-1\"}}}}";
//
//        String s = HttpRequestUtils.doPost(TOKEN_URL, jsonString);
//        System.out.println(s);
//
//    }
//}
