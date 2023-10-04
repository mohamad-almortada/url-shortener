package com.example.urlshrink.exceptions;

import com.example.urlshrink.service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
public class CustomExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(UrlService.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handle(Exception ex,
                                         HttpServletRequest request, HttpServletResponse response) {

        String errorMessage;
        HttpStatus httpStatus;
        switch (ex.getClass().getSimpleName()) {
            case "BadRequest":
                errorMessage = "Bad request: " + ex.getMessage();
                httpStatus = BAD_REQUEST;
                break;
            case "InvalidIdException":
                errorMessage = "Bad Request: " + ex.getMessage();
                httpStatus = BAD_REQUEST;
                break;
            case "UrlNotFoundException":
                errorMessage = "Not Found: " + ex.getMessage();
                httpStatus = NOT_FOUND;
                break;
            default:
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                errorMessage = "Internal Server Error: " + ex.getMessage();
        }


        return ResponseEntity.status(httpStatus).body(errorMessage);
    }
}