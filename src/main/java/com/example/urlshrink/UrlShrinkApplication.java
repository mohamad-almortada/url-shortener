package com.example.urlshrink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class UrlShrinkApplication {

	public static void main(String[] args) {
		SpringApplication.run(UrlShrinkApplication.class, args);
	}

}
