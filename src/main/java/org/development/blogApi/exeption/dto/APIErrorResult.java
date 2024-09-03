package org.development.blogApi.exeption.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class APIErrorResult {
    public int statusCode;
    public LocalDateTime timestamp;
    public String message;

    public APIErrorResult(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
