package com.jresteasy.exception;

import lombok.Getter;

@Getter
public class JRestException extends RuntimeException {
    private final int statusCode;
    private final String responseBody;

    public JRestException(String message, int statusCode, String responseBody) {
        super(message);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public JRestException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 0;
        this.responseBody = null;
    }
}
