package com.hjq.http.config;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2020/04/24
 *    desc   : 日志打印策略
 */
public interface IHttpLogStrategy {

    /**
     * 打印分割线
     */
    default void printLine(String tag) {
        printLog(tag, "----------------------------------------");
    }

    /**
     * 打印日志
     */
    void printLog(String tag, String log);

    /**
     * 打印 Json
     */
    void printJson(String tag, String json);

    /**
     * 打印键值对
     */
    void printKeyValue(String tag, String key, String value);

    /**
     * 打印异常
     */
    void printThrowable(String tag, Throwable throwable);

    /**
     * 打印堆栈
     */
    void printStackTrace(String tag, StackTraceElement[] stackTrace);
}