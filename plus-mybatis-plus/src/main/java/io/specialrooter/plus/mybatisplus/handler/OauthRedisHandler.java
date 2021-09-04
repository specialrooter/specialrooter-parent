package io.specialrooter.plus.mybatisplus.handler;

import io.specialrooter.context.SpringContext;
import io.specialrooter.context.model.AuthDTO;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * 用户中心-注册用户到redis标准定义接口
 */
public interface OauthRedisHandler {
    void setAuthUser(AuthDTO authDTO);

    void refreshUser(Serializable id);

    void loginOut();

    void loginOut(Serializable id);

    Long getCurrentUserId();

    Long getCurrentUserId(Long defaultVal);

    AuthDTO getCurrentUser();

    Long getAppId();

    static HttpServletRequest request() {
        return SpringContext.getRequest();
    }

    private static String getLocalIp() {
        HttpServletRequest request = request();
        String remoteAddr = request.getRemoteAddr();
        String forwarded = request.getHeader("X-Forwarded-For");
        String realIp = request.getHeader("X-Real-IP");

        String ip = null;
        if (realIp == null) {
            if (forwarded == null) {
                ip = remoteAddr;
            } else {
                ip = forwarded.split(",")[0];
            }
        } else {
            if (realIp.equals(forwarded)) {
                ip = realIp;
            } else {
                if (forwarded != null) {
                    forwarded = forwarded.split(",")[0];
                }
                ip = forwarded;
            }
        }
        return ip;
    }
}
