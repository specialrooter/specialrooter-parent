package io.specialrooter.standard.component.exception;

/**
 * //或者继承RuntimeException（运行时异常）
 */
public class OauthException extends RuntimeException {
    public OauthException() {
    }

    public OauthException(String message) {
        super(message);
    }
}
