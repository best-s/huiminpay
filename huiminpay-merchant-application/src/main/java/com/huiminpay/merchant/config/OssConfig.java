package com.huiminpay.merchant.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OssConfig {

    @Value("${oss.aliyun.endpoint}")
    String endpoint;
    @Value("${oss.aliyun.accessKeyId}")
    String accessKeyId;

    @Value("${oss.aliyun.accessKeySecret}")
    String accessKeySecret;
    @Value("${oss.aliyun.domain}")
    String domain;

    @Bean
    public OSS ossClient() {
        return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

}
