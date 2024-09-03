package org.development.blogApi.exeption.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class APIValidationErrorResult {
    public List<APIFieldError> errorsMessages;
}
