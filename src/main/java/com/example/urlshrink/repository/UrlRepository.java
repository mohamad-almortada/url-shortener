package com.example.urlshrink.repository;

import com.example.urlshrink.model.Url;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlRepository extends MongoRepository<Url, String> {
     Url findByShortenedUrl(String shortenedUrl);
     Boolean existsByShortenedUrl(String shortenedUrl);
     Boolean existsByOriginalUrl(String originalUrl);
}
