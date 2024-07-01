package com.quinbay.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.annotation.EnableKafka;

import java.io.File;

@EnableKafka
@EnableCaching
@SpringBootApplication
@EnableDiscoveryClient
public class InventoryManagementApplication {

	public static void main(String[] args) {

		SpringApplication.run(InventoryManagementApplication.class, args);
	}

}