package com.local.loadtest;

import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.util.Timeout;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Fork(1)
public class ApiLoadTestBenchmark {

    private CloseableHttpClient httpClient;
    private ApiConfig config;

    @Setup(Level.Trial)
    public void setup() {
        config = new ApiConfig();
        
        // 配置连接池
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(200);
        connectionManager.setDefaultMaxPerRoute(100);

        // 配置超时
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(config.getConnectionTimeout()))
                .setResponseTimeout(Timeout.ofMilliseconds(config.getSocketTimeout()))
                .build();

        // 创建HTTP客户端
        httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    @TearDown(Level.Trial)
    public void tearDown() throws Exception {
        if (httpClient != null) {
            httpClient.close();
        }
    }

    @Benchmark
    public int testApi() throws Exception {
        HttpUriRequestBase request = createRequest(config);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getCode();
            EntityUtils.consume(response.getEntity());
            return statusCode;
        }
    }

    private HttpUriRequestBase createRequest(ApiConfig config) {
        String url = config.getFullUrl();
        String method = config.getMethod().toUpperCase();
        
        HttpUriRequestBase request = switch (method) {
            case "POST" -> {
                HttpPost post = new HttpPost(url);
                if (!config.getRequestBody().isEmpty()) {
                    post.setEntity(new StringEntity(config.getRequestBody(), ContentType.APPLICATION_JSON));
                }
                yield post;
            }
            case "PUT" -> {
                HttpPut put = new HttpPut(url);
                if (!config.getRequestBody().isEmpty()) {
                    put.setEntity(new StringEntity(config.getRequestBody(), ContentType.APPLICATION_JSON));
                }
                yield put;
            }
            case "PATCH" -> {
                HttpPatch patch = new HttpPatch(url);
                if (!config.getRequestBody().isEmpty()) {
                    patch.setEntity(new StringEntity(config.getRequestBody(), ContentType.APPLICATION_JSON));
                }
                yield patch;
            }
            case "DELETE" -> new HttpDelete(url);
            default -> new HttpGet(url);
        };
        
        // 添加自定义Headers
        addHeaders(request, config.getHeaders());
        
        return request;
    }
    
    private void addHeaders(HttpUriRequestBase request, String headersStr) {
        if (headersStr == null || headersStr.isEmpty()) {
            return;
        }
        
        // 格式: "Header1:Value1,Header2:Value2"
        String[] headerPairs = headersStr.split(",");
        for (String pair : headerPairs) {
            String[] keyValue = pair.split(":", 2);
            if (keyValue.length == 2) {
                request.addHeader(keyValue[0].trim(), keyValue[1].trim());
            }
        }
    }
}
