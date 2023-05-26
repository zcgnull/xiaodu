package com.dataport.wellness.http;

public class HttpIntData<T> {
    /** 返回码 */
    private int code;
    /** 提示语 */
    private String msg;
    /** 数据 */
    private T data;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return msg;
    }

    public T getData() {
        return data;
    }
}
