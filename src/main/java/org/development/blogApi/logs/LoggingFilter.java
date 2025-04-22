package org.development.blogApi.logs;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LoggingFilter implements Filter {

    private final HttpLogRepository logRepo;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // Wrap request and response
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper((HttpServletRequest) request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper((HttpServletResponse) response);

        String uri = wrappedRequest.getRequestURI();
        if (isSwaggerRequest(uri)) {
            chain.doFilter(wrappedRequest, response);
            return;
        }

        LocalDateTime requestTime = LocalDateTime.now();
        String correlationId = UUID.randomUUID().toString();

        try {
            chain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            LocalDateTime responseTime = LocalDateTime.now();
            HttpLog httpLog = new HttpLog();


            httpLog.setCorrelationId(correlationId);
            httpLog.setMethod(wrappedRequest.getMethod());
            httpLog.setUri(wrappedRequest.getRequestURI());
            httpLog.setQueryParams(wrappedRequest.getQueryString());
            httpLog.setRequestBody(getRequestBody(wrappedRequest));
            httpLog.setResponseBody(getResponseBody(wrappedResponse));
            httpLog.setResponseStatus(wrappedResponse.getStatus());
            httpLog.setRequestTime(requestTime);
            httpLog.setResponseTime(responseTime);

            logRepo.save(httpLog);

            // Important: copy the response body back to the original response
            wrappedResponse.copyBodyToResponse();
        }
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        byte[] buf = request.getContentAsByteArray();
        if (buf.length == 0) return null;
        return new String(buf, StandardCharsets.UTF_8);
    }

    private String getResponseBody(ContentCachingResponseWrapper response) {
        byte[] buf = response.getContentAsByteArray();
        if (buf.length == 0) return null;
        return new String(buf, StandardCharsets.UTF_8);
    }

    private boolean isSwaggerRequest(String uri) {
        return uri.startsWith("/swagger") ||
                uri.startsWith("/v3/api-docs") ||
                uri.startsWith("/swagger-ui") ||
                uri.startsWith("/swagger-resources");
    }
}
