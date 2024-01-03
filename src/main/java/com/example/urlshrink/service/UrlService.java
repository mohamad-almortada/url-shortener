package com.example.urlshrink.service;

import com.example.urlshrink.exceptions.DuplicateKeyErrorException;
import com.example.urlshrink.exceptions.InvalidIdException;
import com.example.urlshrink.exceptions.UrlNotFoundException;
import com.example.urlshrink.model.Url;
import com.example.urlshrink.repository.UrlRepository;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.UUID;

@Service
public class UrlService {

    private final UrlRepository urlRepository;
    private final Clock clock;
    private final MongoTemplate mongoTemplate;
    private static final Logger logger = LoggerFactory.getLogger(UrlService.class);
    public UrlService(UrlRepository urlRepository, Clock clock, MongoTemplate mongoTemplate) {
        this.urlRepository = urlRepository;
        this.clock = clock;
        this.mongoTemplate = mongoTemplate;
    }



    public String createUrl(String original) {
        original = fixHttpsPrefixIfMissing(original);

        if (!isUrlValid(original)) {
            throw new InvalidIdException(String.format("Given URL %s is not valid!", original));
        }
        
        if(urlRepository.existsByOriginalUrl(original)) {
            throw new DuplicateKeyErrorException(String.format("The Url %s already exists. Please try another one.", original));
        }

       String shortened = createUniqueURL();
       Url url = new Url(original, shortened, 0);
       urlRepository.save(url);
       return shortened;
    }

    public String retrieveUrl(String shortenedUrl) {
        Url url = getUrlByShortenedUrl(shortenedUrl);

        return url.getOriginalUrl();
    }

    public void updateClickTimestamp(String shortenedUrl) {
        Url url = getUrlByShortenedUrl(shortenedUrl);
        url.setTimestamp(LocalDateTime.now(clock));
        urlRepository.save(url);
    }

    public void updateUrl(String shortenedUrl, String desiredShortenedUrl, Date expireDate) {

        validateGivenShortenedUrl(shortenedUrl);
        Update update = new Update();

        if(expireDate != null) {
            validateGivenExpireDate(expireDate);
            update.set("expireDate", expireDate);
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("shortenedUrl").is(shortenedUrl));
        update.set("shortenedUrl", desiredShortenedUrl);
        mongoTemplate.updateFirst(query, update, Url.class);
    }


    public void deleteUrl(String shortenedUrl) {
        Url url = getUrlByShortenedUrl(shortenedUrl);
        urlRepository.deleteById(url.getId());
    }

// Cleanup expired url entities
    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteExpiredEntities() {
        Date currentDate = Date.from(LocalDateTime.now(clock).toInstant(ZoneOffset.UTC));
        urlRepository.deleteByExpireDateBefore(currentDate);
    }


    public String incrementClickCount(String shortenedUrl) {
        Url url = getUrlByShortenedUrl(shortenedUrl);
        // increment clickCount
        url.setClickCount(url.getClickCount()+1);
        urlRepository.save(url);

        return url.getOriginalUrl();
    }

    private void validateGivenShortenedUrl(String shortenedUrl) {
        String shortenedUrlPattern = "^[a-zA-Z0-9]{6}$";
        if(!(shortenedUrl.trim()).matches(shortenedUrlPattern)) {
            throw new IllegalArgumentException("Invalid given shortened Url. It must be at least 6 characters and contains letters and/or numbers.");
        }
    }

    private void validateGivenExpireDate(Date expireDate) {
        Date today = Date.from(LocalDateTime.now(clock).toInstant(ZoneOffset.UTC));
        if(expireDate.before(today)) {
            throw new IllegalArgumentException("Expire date is in the past. Make sure to enter a valid expire date in future.");
        }
    }
    private Url getUrlByShortenedUrl(String shortenedUrl) {
        Url url = urlRepository.findByShortenedUrl(shortenedUrl);
        if(url == null) {
            throw new UrlNotFoundException(String.format("Url %s not found", shortenedUrl));
        }
        return url;
    }


    private String createUniqueURL() {
       String shortenedUrl = "";
        do {
           shortenedUrl = UUID.randomUUID().toString().substring(0, 6);
       } while(urlRepository.existsByShortenedUrl(shortenedUrl));
        return shortenedUrl;
    }

    private boolean isUrlValid(String originalUrl) {
        return new UrlValidator().isValid(originalUrl);
    }

    private boolean isHttpsMissing(String originalUrl) {
        String beginWithHttpRegex = "^(https?://).*$";
        return !originalUrl.matches(beginWithHttpRegex);
    }

    private String fixHttpsPrefixIfMissing(String originalUrl) {
        if (isHttpsMissing(originalUrl)) {
            return "https://" + originalUrl;
        }
        return originalUrl;
    }
}
