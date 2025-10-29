package com.local.loadtest;

public class ApiConfig {
    private String baseUrl;
    private String endpoint;
    private String method;
    private String requestBody;
    private String headers;
    private int connectionTimeout;
    private int socketTimeout;

    public ApiConfig() {
        this.baseUrl = System.getProperty("api.baseUrl", "http://localhost:8080");
        this.endpoint = System.getProperty("api.endpoint", "/api/v1/test");
        this.method = System.getProperty("api.method", "GET");
        this.requestBody = System.getProperty("api.body", "");
        this.headers = System.getProperty("api.headers", "");
        this.connectionTimeout = Integer.parseInt(System.getProperty("api.connectionTimeout", "5000"));
        this.socketTimeout = Integer.parseInt(System.getProperty("api.socketTimeout", "10000"));
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getMethod() {
        return method;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public String getHeaders() {
        return headers;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public String getFullUrl() {
        return baseUrl + endpoint;
    }
}
