package com.arshd3ep.microservices.api.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.sleuth.autoconfig.zipkin2.ZipkinAutoConfiguration;


@SpringBootApplication(exclude = {ZipkinAutoConfiguration.class})
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

}
