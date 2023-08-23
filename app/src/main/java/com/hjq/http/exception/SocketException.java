package com.hjq.http.exception;

/**
 * @author: zcg
 * @date: 2023/8/22
 * @desc: Socket异常
 */
public final class SocketException extends HttpException {

    public SocketException(String message) {
        super(message);
    }

    public SocketException(String message, Throwable cause) {
        super(message, cause);
    }
}