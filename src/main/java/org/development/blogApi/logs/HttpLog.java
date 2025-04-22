package org.development.blogApi.logs;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "http_logs")
public class HttpLog {
    @Id
    @GeneratedValue
    private Long id;

    private String correlationId;

    private String method;

    private String uri;

//    @Lob
//    @Column(columnDefinition = "TEXT")
    @Column(length = 500)
    private String queryParams;

//    @Lob
//    @Column(columnDefinition = "TEXT")
    @Column(length = 20000)
    private String requestBody;

//    @Lob
//    @Column(columnDefinition = "TEXT")
    @Column(length = 20000)
    private String responseBody;

    private int responseStatus;

    private LocalDateTime requestTime;

    private LocalDateTime responseTime;
}
