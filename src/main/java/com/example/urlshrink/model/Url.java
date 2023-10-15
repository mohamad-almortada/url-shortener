package com.example.urlshrink.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Document(collection = "urls")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Url {
    @Id
    private String id;

    @Indexed(name = "originalUrl",unique = true)
    private String originalUrl;

    @Indexed(name = "shortenedUrl",unique = true)
    private String shortenedUrl;

    private Integer clickCount;

    @CreatedDate
    private Date createdDate;

    @LastModifiedDate
    private Date lastModified;

    private LocalDateTime timestamp;

    private Date expireDate;

    public Url(String original, String shortened, int clickCount) {
        this.originalUrl = original;
        this.shortenedUrl = shortened;
        this.clickCount = clickCount;
        this.expireDate = Date.from(LocalDateTime.now().plusWeeks(1).atZone(ZoneId.systemDefault()).toInstant());
    }
}
