package io.specialrooter.http;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @Author: Ai
 * @Date: 2018/1/12 11:39
 */
public class HttpRequestUtils {

    public static String sendHttps(String url) throws Exception {
        URL url1 = new URL(url);
        URLConnection conn = url1.openConnection();
        if (conn instanceof HttpsURLConnection) {
            SSLContext sc = SSLContext.getInstance("SSL");

            X509TrustManager tm = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };

            sc.init(null, new TrustManager[]{tm}, new java.security.SecureRandom());
            ((HttpsURLConnection) conn).setSSLSocketFactory(sc.getSocketFactory());
            ((HttpsURLConnection) conn).setHostnameVerifier(new HostnameVerifier() {

                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }

            });
        }
        InputStream inputStream = conn.getInputStream();

        System.out.println(inputStream.available());
        return IOUtils.toString(inputStream, "UTF-8");
    }

    public static String doGet(String url) {
        return doGet(url, null, null);
    }

    public static String doGet(String url, Map<String, String> params) {
        return doGet(url, null, params);
    }

    /**
     * get请求
     *
     * @return
     */
    public static String doGet(String url, Map<String, String> params, Map<String, String> headers) {
        try {
            CloseableHttpClient client = HttpClients.createDefault();

            URIBuilder uriBuilder = new URIBuilder(url);

            if (params != null && !params.isEmpty()) {
                params.forEach((x, y) -> {
                    uriBuilder.addParameter(x, y);
                });
            }

            //发送get请求
            HttpGet request = new HttpGet(uriBuilder.build());

            if (headers != null && !headers.isEmpty()) {
                headers.forEach((x, y) -> {
                    request.addHeader(x, y);
                });
            }


            CloseableHttpResponse response = client.execute(request);

            /**请求发送成功，并得到响应**/
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                /**读取服务器返回过来的json字符串数据**/
                String strResult = EntityUtils.toString(response.getEntity());

                return strResult;
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * post请求(用于key-value格式的参数)
     *
     * @param url
     * @param params
     * @return
     */
    public static String doPost(String url, Map params) {

        BufferedReader in = null;
        try {
            // 定义HttpClient
            CloseableHttpClient client = HttpClients.createDefault();
            // 实例化HTTP方法
            HttpPost request = new HttpPost();
            request.setURI(new URI(url));

            //设置参数
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            for (Iterator iter = params.keySet().iterator(); iter.hasNext(); ) {
                String name = (String) iter.next();
                String value = String.valueOf(params.get(name));
                nvps.add(new BasicNameValuePair(name, value));

                //System.out.println(name +"-"+value);
            }
            request.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));

            CloseableHttpResponse response = client.execute(request);
            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {    //请求成功
                in = new BufferedReader(new InputStreamReader(response.getEntity()
                        .getContent(), "utf-8"));
                StringBuffer sb = new StringBuffer("");
                String line = "";
                String NL = System.getProperty("line.separator");
                while ((line = in.readLine()) != null) {
                    sb.append(line + NL);
                }

                in.close();

                return sb.toString();
            } else {    //
                System.out.println("状态码：" + code);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    /**
     * post请求（用于请求json格式的参数）
     *
     * @param url
     * @param params
     * @return
     */
    public static String doPost(String token, String url, String params) throws Exception {

        CloseableHttpClient httpclient = /*new SSLClient();*/HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);// 创建httpPost
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Authorization", token);
        String charSet = "UTF-8";
        StringEntity entity = new StringEntity(params, charSet);
        httpPost.setEntity(entity);
        CloseableHttpResponse response = null;

        try {

            response = httpclient.execute(httpPost);
            StatusLine status = response.getStatusLine();
            int state = status.getStatusCode();
            if (state == HttpStatus.SC_OK) {
                HttpEntity responseEntity = response.getEntity();
                String jsonString = EntityUtils.toString(responseEntity);
                return jsonString;
            } else {
                System.err.println("请求返回:" + state + "(" + url + ")");
            }
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * post请求（用于请求json格式的参数）
     *
     * @param url
     * @param params
     * @return
     */
    public static String doPost(String url, String params) throws Exception {

        CloseableHttpClient httpclient = /*new SSLClient();*/HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);// 创建httpPost
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-Type", "application/json");
        String charSet = "UTF-8";
        StringEntity entity = new StringEntity(params, charSet);
        entity.setContentType("application/json");
        httpPost.setEntity(entity);
        CloseableHttpResponse response = null;

        try {

            response = httpclient.execute(httpPost);
            StatusLine status = response.getStatusLine();
            int state = status.getStatusCode();
            if (state == HttpStatus.SC_OK) {
                HttpEntity responseEntity = response.getEntity();
                String jsonString = EntityUtils.toString(responseEntity);
                return jsonString;
            } else {
                System.err.println("请求返回:" + state + "(" + url + ")");
            }
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static JSONObject postJSONObject(String url, JSONObject params) {
        String s = HttpRequestUtils.postJSON(url, params);
        return JSONObject.parseObject(s);
    }

    public static String postJSON(String url, JSONObject params) {
        //String url = "http://120.24.99.26:8090/trans";

        String respContent = null;

        //JSONObject params = new JSONObject();
        //params.put("domain", "报销清单");
        //params.put("fields", "报销清单\n项目\n消费类型\n消费金额\n往返行程\n消费日期\n备注\n上传图片");

        HttpPost httpPost = new HttpPost(url);
        CloseableHttpClient client = HttpClients.createDefault();
        StringEntity entity = new StringEntity(params.toString(), "utf-8");//解决中文乱码问题
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);

        try {
            CloseableHttpResponse response = client.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity he = response.getEntity();
                respContent = EntityUtils.toString(he, "UTF-8");

                return respContent;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
