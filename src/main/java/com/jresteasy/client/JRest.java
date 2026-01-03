package com.jresteasy.client;

import org.springframework.http.HttpMethod;

public class JRest {

    public static JRestRequest get(String url) {
        return new JRestRequest(url, HttpMethod.GET);
    }

    public static JRestRequest post(String url) {
        return new JRestRequest(url, HttpMethod.POST);
    }

    public static JRestRequest put(String url) {
        return new JRestRequest(url, HttpMethod.PUT);
    }

    public static JRestRequest delete(String url) {
        return new JRestRequest(url, HttpMethod.DELETE);
    }

    // Helper to start with a custom method if needed
    public static JRestRequest method(String url, HttpMethod method) {
        return new JRestRequest(url, method);
    }
}
