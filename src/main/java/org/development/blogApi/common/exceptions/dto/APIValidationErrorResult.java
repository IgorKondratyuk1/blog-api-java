package org.development.blogApi.common.exceptions.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class APIValidationErrorResult {
    public List<APIFieldError> errorsMessages;
}
