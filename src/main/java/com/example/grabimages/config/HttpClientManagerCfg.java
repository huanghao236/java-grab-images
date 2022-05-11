package com.example.grabimages.config;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpClientManagerCfg {

    @Bean
    public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
        // 创建连接管理器
        PoolingHttpClientConnectionManager httpClientConnectionManager = new PoolingHttpClientConnectionManager();
        // 设置最大连接数
        httpClientConnectionManager.setMaxTotal(100);
        // 设置每个并发连接数
        httpClientConnectionManager.setDefaultMaxPerRoute(50);
        return httpClientConnectionManager;
    }
}
