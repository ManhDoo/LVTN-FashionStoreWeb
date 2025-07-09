package com.example.FashionStoreBE;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class FashionStoreBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(FashionStoreBeApplication.class, args);
	}

}
