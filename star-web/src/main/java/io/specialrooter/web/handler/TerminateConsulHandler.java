package io.specialrooter.web.handler;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PreDestroy;
import java.io.IOException;

//@Component
@Slf4j
public class TerminateConsulHandler {
    private static OkHttpClient client = new OkHttpClient();
    @Value("${spring.cloud.consul.host:127.0.0.1}")
    private String host;
    @Value("${spring.cloud.consul.port:8500}")
    private String port;
    @Value("${spring.cloud.consul.discovery.instance-id:}")
    private String instanceId;

    @PreDestroy
    public void destroy() {
        try {
            log.info("cleaning consul instance is "+instanceId);
            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(mediaType,"");
            Request request = new Request.Builder().url("http://"+host+":"+port+"/v1/agent/service/deregister/"+instanceId).put(body).build();
            Call call = client.newCall(request);
            try {
                Response response = call.execute();
                String s = response.body().string();

                if (s.length() > 0) {
                }
                log.info("cleaning consul instance ok, status "+s);
            } catch (IOException e) {
            }

        } catch (Exception e) {
            log.info("cleaning consul exception: "+e.getMessage());
        }
    }
}
