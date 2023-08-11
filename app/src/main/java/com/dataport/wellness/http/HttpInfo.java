package com.dataport.wellness.http;


public class HttpInfo<T> {

    /** 返回码 */
    private String code;
    /** 提示语 */
    private String msg;
    /** 数据 */
    private T info;

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return msg;
    }

    public T getInfo() {
        return info;
    }


}