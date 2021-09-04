package io.specialrooter.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel("标准报文返回模型")
@Data
public class ResponseModel<T> implements Serializable {
    @ApiModelProperty(value = "响应是否成功", required = true)
    private Boolean success = true;
    @ApiModelProperty(value = "响应消息")
    private String message;
    @ApiModelProperty(value = "响应编码")
    private String code;
    @ApiModelProperty(value = "响应内容，根据需求具体定义")
    private T data;

    public ResponseModel(Boolean success, String message, ResponseCode code, T data) {
        this.success = success;
        this.message = message == null ? code.reasonPhrase() : message;
        this.code = code.value();
        this.data = data;
    }

    public ResponseModel(Boolean success, String message, String code, T data) {
        this.success = success;
        this.message = message;
        this.code = code;
        this.data = data;
    }

    public ResponseModel(Boolean success, String message, ResponseCode code) {
        this.success = success;
        this.message = message == null ? code.reasonPhrase() : message;
        this.code = code.value();
    }

    public ResponseModel(Boolean success, String message, String code) {
        this.success = success;
        this.message = message;
        this.code = code;
    }

    public static <T> ResponseModel success(T data) {
        return new ResponseModel(true, null, ResponseCode.SUCCESS, data);
    }

    public static ResponseModel failure(String message) {
        return new ResponseModel(false, message, ResponseCode.FAILURE);
    }

    public static ResponseModel failure(String code, String message) {
        return new ResponseModel(false, message, code);
    }

    public static <T> ResponseModel bool(boolean bool,String message) {
        if (bool) {
            return success(null);
        } else {
            return failure(message);
        }
    }

    public static <T> ResponseModel bool(boolean bool, T data,String message) {
        if (bool) {
            if(data instanceof String){
                return success(null);
            }else{
                return success(data);
            }
        } else {
            return failure(message);
        }
    }
}
