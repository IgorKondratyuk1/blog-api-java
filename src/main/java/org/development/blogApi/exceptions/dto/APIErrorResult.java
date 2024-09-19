package org.development.blogApi.exceptions.dto;


import lombok.Data;

import java.time.LocalDateTime;

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
