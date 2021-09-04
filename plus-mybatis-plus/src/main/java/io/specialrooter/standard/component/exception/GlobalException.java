package io.specialrooter.standard.component.exception;

import lombok.Data;

@Data
public class GlobalException extends RuntimeException {
    private int code;
    private String msg;

    public GlobalException(IEnumException ex, String msg) {
        super(ex.getMessage());
        this.code = ex.getCode();
        this.msg = msg;
    }

    public GlobalException(IEnumException ex) {
        super(ex.getMessage());
        this.code = ex.getCode();
        this.msg = ex.getMessage();
    }

    public GlobalException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }
}