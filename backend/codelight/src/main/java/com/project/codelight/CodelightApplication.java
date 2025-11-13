package com.project.codelight;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class CodelightApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodelightApplication.class, args);
	}

}
