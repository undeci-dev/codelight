package com.project.codelight.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aws.cloudfront")
public record CloudFrontProperties(
    String domain
) {
}
