package com.hjq.http.exception;

/**
 * @author: zcg
 * @date: 2023/8/22
 * @desc: 协议错误
 */
public final class ProtocolException extends HttpException {

    public ProtocolException(String message) {
        super(message);
    }

    public ProtocolException(String message, Throwable cause) {
        super(message, cause);
    }
}