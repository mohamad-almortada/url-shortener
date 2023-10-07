package com.example.urlshrink.controller;

import com.example.urlshrink.model.OriginalUrlRequestDto;
import com.example.urlshrink.service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/api/v1/")
public class UrlController {
    private UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @GetMapping("/{shortenedUrl}")
    public ResponseEntity<Object> redirectTo(@PathVariable @Size(min = 2) @NotNull String shortenedUrl, HttpServletRequest request, HttpServletResponse response) {
        String originalUrl = this.urlService.retrieveUrl(shortenedUrl);
        String xRedirect = request.getHeader("X-Redirect");
        if (xRedirect != null) {
            response.setHeader("Location", originalUrl);
            urlService.incrementClickCount(shortenedUrl);
            return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).build();
        }
        return ResponseEntity.ok(originalUrl);
    }

    @PostMapping("shortened-url/")
    public ResponseEntity<String> createOne(@Valid @RequestBody OriginalUrlRequestDto urlRequestDto) {
            String createdUrl = this.urlService.createUrl(urlRequestDto.getOriginalUrl());
            return ResponseEntity.ok(createdUrl);
    }

}
