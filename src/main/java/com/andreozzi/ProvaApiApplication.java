package com.andreozzi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableScheduling
public class ProvaApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProvaApiApplication.class, args);
	}

    @Bean
    RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder.build();
}
}

