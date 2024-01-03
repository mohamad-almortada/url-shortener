package com.example.urlshrink.controller;

import com.example.urlshrink.model.dtos.OriginalUrlRequestDto;
import com.example.urlshrink.model.dtos.UpdateUrlDto;
import com.example.urlshrink.service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Validated
@RequestMapping("/api/v1/")
public class UrlController {
    private final UrlService urlService;

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

    @PatchMapping
    public ResponseEntity<String> updateUrl(@Valid @RequestBody UpdateUrlDto updateUrlDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            String errorMessages = fieldErrors.stream().map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining("\n"));

            return ResponseEntity.badRequest().body(errorMessages);
        }
        this.urlService.updateUrl(updateUrlDto.getShortenedUrl(), updateUrlDto.getDesiredShortenedUrl(), updateUrlDto.getExpireDate());
        return ResponseEntity.ok("Url entity successfully updated.");
    }

    @DeleteMapping("/delete/{shortenedUrl}")
    public ResponseEntity<String> deleteUrl(@NotNull @PathVariable  String shortenedUrl) {
        this.urlService.deleteUrl(shortenedUrl);
        return ResponseEntity.ok("Url entity successfully deleted.");
    }

    @PostMapping("shortened-url/")
    public ResponseEntity<String> createOne(@Valid @RequestBody OriginalUrlRequestDto urlRequestDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            String errorMessages = fieldErrors.stream().map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining("\n"));

            return ResponseEntity.badRequest().body(errorMessages);
        }
            String createdUrl = this.urlService.createUrl(urlRequestDto.getOriginalUrl());
            return ResponseEntity.ok(createdUrl);
    }

}
