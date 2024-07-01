package com.example.inventory_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class InventoryGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryGatewayApplication.class, args);
	}

}