package com.dataport.wellness.api.smalldu;

public class XiaoDuResultDto<T> {
    private Integer code;
    private String msg;

    private T Data;

    public T getData() {
        return Data;
    }

    public void setData(T data) {
        Data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
