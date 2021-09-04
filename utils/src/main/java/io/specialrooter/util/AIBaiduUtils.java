package io.specialrooter.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class AIBaiduUtils {

    /**
     * 获取权限token
     * @return 返回示例：
     * {
     * "access_token": "24.460da4889caad24cccdb1fea17221975.2592000.1491995545.282335-1234567",
     * "expires_in": 2592000
     * }
     */
    public static String getAuth() {
        // 官网获取的 API Key 更新为你注册的
        String clientId = "o9aAz4DRgiNvAVVMBLdzVvLx";
        // 官网获取的 Secret Key 更新为你注册的
        String clientSecret = "vOhd6qCLbFetlKoU7MGCcUGZg8IxPxdt";
        return getAuth(clientId, clientSecret);
    }

    /**
     * 获取API访问token
     * 该token有一定的有效期，需要自行管理，当失效时需重新获取.
     * @param ak - 百度云官网获取的 API Key
     * @param sk - 百度云官网获取的 Securet Key
     * @return assess_token 示例：
     * "24.460da4889caad24cccdb1fea17221975.2592000.1491995545.282335-1234567"
     */
    public static String getAuth(String ak, String sk) {
        // 获取token地址
        String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
        String getAccessTokenUrl = authHost
                // 1. grant_type为固定参数
                + "grant_type=client_credentials"
                // 2. 官网获取的 API Key
                + "&client_id=" + ak
                // 3. 官网获取的 Secret Key
                + "&client_secret=" + sk;
        try {
            URL realUrl = new URL(getAccessTokenUrl);
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.err.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String result = "";
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            /**
             * 返回结果示例
             */
            System.err.println("result:" + result);
            JSONObject jsonObject = JSON.parseObject(result);
            String access_token = jsonObject.getString("access_token");
            return access_token;
        } catch (Exception e) {
            System.err.printf("获取token失败！");
            e.printStackTrace(System.err);
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        String token = getAuth();

        String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/business_license";
        String tt = "https://scce-cos-obs.obs.cn-south-1.myhuaweicloud.com:443/7fbf9bbdad8b4e9f873eec96e01b9a83.jpg";

        String url2 = "https://aip.baidubce.com/rest/2.0/ocr/v1/idcard";
        String tt2 = "https://gimg2.baidu.com/image_search/src=http%3A%2F%2F00.imgmini.eastday.com%2Fmobile%2F20180205%2F20180205194505_5e61a10be7e2d03362ae2e6989519dab_4.jpeg&refer=http%3A%2F%2F00.imgmini.eastday.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1617089671&t=12252ff306bc3094a7ec7b7b254bec2b";


        String post = AIBaiduHttpUtils.post(url, token, "url=" + tt);
        System.out.println(post);
        String post2 = AIBaiduHttpUtils.post(url2, token, "url=" + tt2);
        System.out.println(post2);
    }
}
