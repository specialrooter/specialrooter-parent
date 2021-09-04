package io.specialrooter.plus.mybatisplus.handler;

import io.specialrooter.context.model.AuthDTO;
import io.specialrooter.context.util.ApiUtils;

import java.io.Serializable;

public class OauthRedisDefaultHandler implements OauthRedisHandler {
    @Override
    public void setAuthUser(AuthDTO authDTO) {

    }

    @Override
    public void refreshUser(Serializable id) {

    }

    @Override
    public void loginOut() {

    }

    @Override
    public void loginOut(Serializable id) {

    }

    @Override
    public Long getCurrentUserId() {
        return ApiUtils.getCurrentUserId();
    }

    @Override
    public Long getCurrentUserId(Long defaultVal) {
        return ApiUtils.getCurrentUserId(defaultVal);
    }

    @Override
    public AuthDTO getCurrentUser() {
        return null;
    }

    @Override
    public Long getAppId() {
        return null;
    }
}
