package io.specialrooter.message;

/**
 * Project me-parent
 * Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights reserved.
 * package org.me.framework.util
 * Created by Me on 2015-12-05.
 */


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
@Deprecated
@ApiModel("标准返回模型")
public class MessageResponse2<T> implements Serializable {
    @ApiModelProperty(value = "业务数据返回")
    private T data;
    @ApiModelProperty(value = "成功/失败", required = true)
    private Boolean success = true;
    @ApiModelProperty(value = "消息")
    private String msg;
    @ApiModelProperty(value = "状态码", required = true)
    private Integer status = MessageState.SUCCESS.value();
    @ApiModelProperty(value = "兼容友店APP接口接收参数", required = true)
    private int state = 0;

    public MessageResponse2() {
        super();
    }

/*    public String toJSONString(){
        return JSON.toJSONString(this);
    }*/

    protected MessageResponse2(boolean success, Integer status, String msg, T data) {
        super();
        this.success = success;
        this.status = status;
        this.state = status;
        this.msg = msg;
        this.data = (T) data;
    }

    public static MessageResponse2 bool(boolean bool, String data, String error) {
        if (bool) {
            return success(data);
        } else {
            return error(error);
        }
    }

    public static MessageResponse2 bool(boolean bool, String message) {
        if (bool) {
            return success(message + "成功！");
        } else {
            return error(message + "失败！");
        }
    }

//    public static MessageResponse bool(boolean bool,String error){
//        if(bool){
//            return success();
//        }else{
//            return error(error);
//        }
//    }

    public static MessageResponse2 bool(boolean bool, Object data, String error) {
        if (bool) {
            return success(data);
        } else {
            return error(error);
        }
    }

    public static MessageResponse2 success() {
        return new MessageResponse2(MessageState.SUCCESS);
    }


    public static MessageResponse2 success(String msg) {
        return new MessageResponse2(msg).setData(false);
    }

    public static <T> MessageResponse2 success(T data) {
        return new MessageResponse2(data);
    }

    public static MessageResponse2 success(MessageState status) {
        return new MessageResponse2(status);
    }

    public static <T> MessageResponse2 success(T data, MessageState status) {
        return new MessageResponse2(status, true, data);
    }

    public static <T> MessageResponse2 success(T data, MessageState state, String msg) {
        return new MessageResponse2(state, true, data, msg);
    }

    public static <T> MessageResponse2 success(T data, String msg) {
        return new MessageResponse2(MessageState.SUCCESS, true, data, msg);
    }

    public static <T> MessageResponse2 error(T data, String msg) {
        return new MessageResponse2(MessageState.FORECASTING_ERROR, false, data, msg);
    }

    public static <T> MessageResponse2 error(T data, MessageState state, String msg) {
        return new MessageResponse2(state, false, data, msg);
    }


    public static MessageResponse2 error(String msg) {
        return new MessageResponse2(MessageState.FORECASTING_ERROR, msg);
    }

    public static <T> MessageResponse2 error(T data) {
        return new MessageResponse2(MessageState.FORECASTING_ERROR, false, data);
    }

    public static MessageResponse2 error(MessageState status) {
        return new MessageResponse2(status);
    }

    public static MessageResponse2 error(MessageState status, String msg) {
        return new MessageResponse2(status, false, msg);
    }

    public static MessageResponse2 error(int status, String msg) {
        return new MessageResponse2(status, msg);
    }

    public MessageResponse2(String msg, Long waste) {
        super();
        this.msg = msg;
    }

    public MessageResponse2(String msg) {
        super();
        this.msg = msg;
    }

    public MessageResponse2(T data, Long waste) {
        super();
        this.data = (T) data;
    }

    public MessageResponse2(T data) {
        super();
        this.data = data;
    }

    public MessageResponse2(T data, String msg, Long waste) {
        super();
        this.data = data;
        this.msg = msg;
    }

    public MessageResponse2(T data, String msg) {
        super();
        this.data = data;
        this.msg = msg;
    }

    public MessageResponse2(MessageState messageStatus, boolean success, String msg) {
        super();
        this.status = messageStatus.value();
        this.success = success;
        this.msg = msg;
    }

    public MessageResponse2(MessageState messageStatus, boolean success, T data, String msg) {
        super();
        this.status = messageStatus.value();
        this.success = success;
        this.data = data;
        this.msg = msg;
    }

    public MessageResponse2(Integer errorCode, String errorMsg, Long waste) {
        super();
        this.success = false;
        this.status = errorCode;
        this.msg = errorMsg;
    }

    public MessageResponse2(Integer errorCode, String errorMsg) {
        super();
        this.msg = errorMsg;
        this.status = errorCode;
        this.success = false;
    }

    public MessageResponse2(MessageState messageStatus, Long waste) {
        super();
        this.msg = messageStatus.reasonPhrase();
        this.status = messageStatus.value();
        this.success = messageStatus.value() == 200 ? true : false;
    }

    public MessageResponse2(MessageState messageStatus, boolean success, T data) {
        super();
        this.data = data;
        this.status = messageStatus.value();
        this.success = success;
        this.msg = messageStatus.reasonPhrase();
    }

    public MessageResponse2(MessageState messageStatus, String msg) {
        super();
        this.msg = msg;
        this.status = messageStatus.value();
        this.success = messageStatus.value() == 200 ? true : false;
    }

    public MessageResponse2(MessageState messageStatus) {
        super();
        this.msg = messageStatus.reasonPhrase();
        this.status = messageStatus.value();
        this.success = messageStatus.value() == 200 ? true : false;
    }

    public T getData() {
        return data;
    }

    public MessageResponse2 setData(T data) {
        this.data = data;
        return this;
    }

    public Boolean getSuccess() {
        return success;
    }

    public MessageResponse2<T> setSuccess(Boolean success) {
        this.success = success;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public MessageResponse2<T> setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public MessageResponse2<T> setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public int getState() {
        return state;
    }

    public MessageResponse2<T> setState(int state) {
        this.state = state;
        return this;
    }

    @Override
    public String toString() {
        return "MessageResponse{" +
                "data=" + data +
                ", success=" + success +
                ", msg='" + msg + '\'' +
                ", status=" + status +
                '}';
    }
}

