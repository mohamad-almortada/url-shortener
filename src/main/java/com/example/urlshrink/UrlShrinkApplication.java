package com.example.urlshrink;

import com.example.urlshrink.configuration.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
@EnableConfigurationProperties(RsaKeyProperties.class)
public class UrlShrinkApplication {

	public static void main(String[] args) {
		SpringApplication.run(UrlShrinkApplication.class, args);
	}

}
