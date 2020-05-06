package io.specialrooter.http.ssl.factory;

import io.specialrooter.http.ssl.DefaultHostnameVerifier;
import io.specialrooter.http.ssl.DefaultTrustManager;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/**
 * @Author: Ai
 * @Date: 2018/1/12 15:02
 */
public class SSL {

    public static void factory(HttpsURLConnection httpsURLConnection) {
        String jvmVersion = System.getProperty("java.version");
        if (jvmVersion.contains("1.6.")) {
            httpsURLConnection.setSSLSocketFactory(new TLSSocketConnectionFactory());
        } else {
            try {
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, new TrustManager[]{new DefaultTrustManager()}, new java.security.SecureRandom());

                httpsURLConnection.setSSLSocketFactory(sc.getSocketFactory());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
        }
    }

    public static void hostnameVerifier(HttpsURLConnection httpsURLConnection) {
        httpsURLConnection.setHostnameVerifier(new DefaultHostnameVerifier());
    }

    public static void is(URLConnection urlConnection) {
        if (urlConnection instanceof HttpsURLConnection) {
            factory((HttpsURLConnection) urlConnection);
            hostnameVerifier((HttpsURLConnection) urlConnection);
        }
    }
}
