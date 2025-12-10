package com.project.codelight;

import com.project.codelight.global.config.CloudFrontProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(CloudFrontProperties.class)
@SpringBootApplication
public class CodelightApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodelightApplication.class, args);
	}

}
