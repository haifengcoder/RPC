package com.ghf.conmon;

import java.io.Serializable;

public class RpcResponse implements Serializable {
    private int code;
    private String message;
    private Object data;

    public RpcResponse(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    public static RpcResponse success(Object data)
    {
        return new RpcResponse(200, "success", data);
    }
    public static RpcResponse fail()
    {
        return new RpcResponse(500, "fail", null);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
