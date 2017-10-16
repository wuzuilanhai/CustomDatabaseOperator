package com.biubiu.constant;

/**
 * Created by zhanghaibiao on 2017/9/6.
 */
public enum  DbResponse {
    CONNECT_SUCCESS(200L,"connect success"), CONNECT_FAIL(400L,"connect fail"),
    CLOSE_SUCCESS(200L,"close success"), CLOSE_FAIL(400L,"close fail");

    DbResponse(long code, String message) {
        this.code = code;
        this.message = message;
    }

    private long code;

    private String message;

    public long getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
