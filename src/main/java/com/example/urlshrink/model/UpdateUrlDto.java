package com.example.urlshrink.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUrlDto {
    @NotNull
    @Size(min = 6, max = 6, message = "shortened url must have length of 6 characters")
    private String shortenedUrl;

    @Size(min = 6, max = 6, message = "shortened url must have length of 6 characters")
    private String desiredShortenedUrl;

    private Date expireDate;
}
