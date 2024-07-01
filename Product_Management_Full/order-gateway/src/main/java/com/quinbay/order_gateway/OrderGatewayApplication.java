package com.quinbay.order_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class OrderGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderGatewayApplication.class, args);
	}

}
