package com.sky.config;


import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类，用于创建AliOssUtil对象
 */
@Configuration
@Slf4j
public class OssConfiguration {

    @Bean
    @ConditionalOnMissingBean // 当容器中没有AliOssUtil对象时，才会创建
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties) {
        log.info("开始创建阿里云文件上传工具类对象：{}",aliOssProperties);
        // 创建AliOssUtil对象
        return new AliOssUtil(
                // 阿里云OSS的endpoint地址
                aliOssProperties.getEndpoint(),
                // 阿里云OSS的accessKeyId
                aliOssProperties.getAccessKeyId(),
                // 阿里云OSS的accessKeySecret
                aliOssProperties.getAccessKeySecret(),
                // 阿里云OSS的bucketName
                aliOssProperties.getBucketName()
        );
    }
}
