package com.example.urlshrink.controller;

import com.example.urlshrink.model.UrlRequestDto;
import com.example.urlshrink.service.UrlService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/")
public class UrlController {
    private UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @GetMapping("/{shortened}")
    public ResponseEntity<String> retrieve(@PathVariable String shortened) {
        return ResponseEntity.ok(this.urlService.retrieveUrl(shortened));
    }
    @PostMapping("/shorten/")
    public ResponseEntity<String> createOne(@RequestBody UrlRequestDto urlRequestDto) {
            String createdUrl = this.urlService.createUrl(urlRequestDto.getUrl());
            return ResponseEntity.ok(createdUrl);
    }


}
