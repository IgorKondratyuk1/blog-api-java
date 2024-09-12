package org.development.blogApi.e2e.helpers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TestTokensPairData {
    private String accessToken;
    private String refreshToken;
}
