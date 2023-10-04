package com.example.urlshrink.service;

import com.example.urlshrink.exceptions.DuplicateKeyErrorException;
import com.example.urlshrink.exceptions.InvalidIdException;
import com.example.urlshrink.exceptions.UrlNotFoundException;
import com.example.urlshrink.model.Url;
import com.example.urlshrink.repository.UrlRepository;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class UrlService {

    private UrlRepository urlRepository;
    private static final Logger logger = LoggerFactory.getLogger(UrlService.class);
    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public String createUrl(String original) {
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
        Url url = urlRepository.findByShortenedUrl(shortenedUrl);
        if(url == null) {
            throw new UrlNotFoundException(String.format("Url %s not found", shortenedUrl));
        }
        return url.getOriginalUrl();
    }

    private boolean isUrlValid(String url) {
        String myUrl = url;
        String beginWithHttpRegex = "^(https?://).*$";
        if(!url.matches(beginWithHttpRegex)) {
            myUrl = "https://" + url;
        }
        return new UrlValidator().isValid(myUrl);
    }

    private String createUniqueURL() {
       String shortenedUrl = "";
        do {
           shortenedUrl = UUID.randomUUID().toString().substring(0, 5);
       } while(urlRepository.existsByShortenedUrl(shortenedUrl));
        return shortenedUrl;
    }
}