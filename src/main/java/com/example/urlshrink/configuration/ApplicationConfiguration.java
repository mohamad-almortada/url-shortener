package com.example.urlshrink.configuration;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;


@Configuration
public class ApplicationConfiguration extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongo.uri}")
    private String mongoConnectionString;

    @Value("${spring.data.mongo.db}")
    private String mongoDBName;

    @Bean
    public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString(mongoConnectionString);
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        return MongoClients.create(mongoClientSettings);
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongoClient(), mongoDBName);
    }

    @Override
    protected String getDatabaseName() {
        return mongoDBName;
    }
    @Override
    protected boolean autoIndexCreation() {
        return true;
    }
}
