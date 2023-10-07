package com.example.urlshrink.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class OriginalUrlRequestDto {
    @NotNull
    @Size(min = 4, message = "original URL must have at least length of 4")
    private String originalUrl;
}
