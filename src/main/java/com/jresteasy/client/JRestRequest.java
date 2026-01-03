package com.jresteasy.client;

import com.jayway.jsonpath.JsonPath;
import com.jresteasy.exception.JRestException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.function.Consumer;

public class JRestRequest {

    private final String url;
    private final HttpMethod method;
    private final RestClient.Builder restClientBuilder;
    private Object body;
    private int retryCount = 0;
    private int timeoutMs = 5000;
    private boolean debug = false;

    public JRestRequest(String url, HttpMethod method) {
        this.url = url;
        this.method = method;
        this.restClientBuilder = RestClient.builder();
    }

    public JRestRequest header(String key, String value) {
        this.restClientBuilder.defaultHeader(key, value);
        return this;
    }

    public JRestRequest headers(Consumer<HttpHeaders> headersConsumer) {
        this.restClientBuilder.defaultHeaders(headersConsumer);
        return this;
    }

    public JRestRequest body(Object body) {
        this.body = body;
        return this;
    }

    public JRestRequest retry(int count) {
        this.retryCount = count;
        return this;
    }

    public JRestRequest timeout(int ms) {
        this.timeoutMs = ms;
        return this;
    }

    public JRestRequest debug() {
        this.debug = true;
        return this;
    }

    // --- Execution Methods ---

    public <T> T as(Class<T> responseType) {
        return execute(responseType);
    }

    public String asString() {
        return execute(String.class);
    }

    public <T> T path(String jsonPath) {
        String json = asString();
        try {
            return JsonPath.read(json, jsonPath);
        } catch (Exception e) {
            throw new JRestException("Failed to extract JSON path: " + jsonPath, e);
        }
    }

    // --- Core Logic ---

    private <T> T execute(Class<T> responseType) {
        RestClient client = buildClient();

        if (debug) {
            System.out.println(">>> JRest Executing: " + method + " " + url);
            if (body != null)
                System.out.println(">>> Body: " + body);
        }

        int attempts = 0;
        Exception lastException = null;

        while (attempts <= retryCount) {
            try {
                RestClient.RequestBodySpec spec = client.method(method).uri(url);

                if (body != null) {
                    spec.contentType(MediaType.APPLICATION_JSON).body(body);
                }

                return spec.retrieve()
                        .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), (req, resp) -> {
                            String errorBody = new String(resp.getBody().readAllBytes());
                            throw new JRestException("API Error: " + resp.getStatusCode(), resp.getStatusCode().value(),
                                    errorBody);
                        })
                        .body(responseType);

            } catch (Exception e) {
                lastException = e;
                attempts++;
                if (attempts <= retryCount) {
                    if (debug)
                        System.out.println(">>> Retry attempt " + attempts + " failed. Retrying...");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        }

        if (lastException instanceof JRestException) {
            throw (JRestException) lastException;
        }
        throw new JRestException("Request failed after " + retryCount + " retries", lastException);
    }

    private RestClient buildClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(timeoutMs);
        factory.setReadTimeout(timeoutMs);

        return restClientBuilder
                .requestFactory(factory)
                .build();
    }
}
