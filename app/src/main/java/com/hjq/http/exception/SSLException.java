package com.hjq.http.exception;

/**
 * @author: zcg
 * @date: 2023/8/21
 * @desc: 证书验证异常
 */
public final class SSLException extends HttpException {

    public SSLException(String message) {
        super(message);
    }

    public SSLException(String message, Throwable cause) {
        super(message, cause);
    }
}