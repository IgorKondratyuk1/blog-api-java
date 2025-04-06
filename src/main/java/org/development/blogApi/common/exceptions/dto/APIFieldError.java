package org.development.blogApi.common.exceptions.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class APIFieldError {
    public String message;
    public String field;
}
