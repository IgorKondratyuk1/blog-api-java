package org.development.blogApi.user.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;


@Data
@AllArgsConstructor
public class ViewUserDto {
    private String id;
    private String login;
    private String email;
//    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SS+XXX", timezone="Europe/Kiev")
//    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
    private LocalDateTime createdAt;
}
