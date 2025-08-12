package com.smarthome.temperatureapi;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
@Component
public class RequestLoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Skip logging for actuator endpoints to reduce noise
        if (httpRequest.getRequestURI().startsWith("/actuator")) {
            chain.doFilter(request, response);
            return;
        }

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(httpRequest);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(httpResponse);

        long startTime = System.currentTimeMillis();

        try {
            // Log incoming request
            logRequest(wrappedRequest);

            // Process the request
            chain.doFilter(wrappedRequest, wrappedResponse);

            // Log outgoing response
            logResponse(wrappedResponse, System.currentTimeMillis() - startTime);

        } finally {
            // Important: copy body content back to response
            wrappedResponse.copyBodyToResponse();
        }
    }

    private void logRequest(ContentCachingRequestWrapper request) {
        StringBuilder requestLog = new StringBuilder();
        requestLog.append("\n=== INCOMING REQUEST ===\n");
        requestLog.append("Method: ").append(request.getMethod()).append("\n");
        requestLog.append("URI: ").append(request.getRequestURI());

        if (request.getQueryString() != null) {
            requestLog.append("?").append(request.getQueryString());
        }
        requestLog.append("\n");

        requestLog.append("Remote Address: ").append(request.getRemoteAddr()).append("\n");

        // Log headers
        requestLog.append("Headers:\n");
        Collections.list(request.getHeaderNames()).forEach(headerName ->
                requestLog.append("  ").append(headerName).append(": ")
                        .append(request.getHeader(headerName)).append("\n"));

        // Log request body for POST/PUT requests
        if ("POST".equals(request.getMethod()) || "PUT".equals(request.getMethod())) {
            byte[] content = request.getContentAsByteArray();
            if (content.length > 0) {
                String body = new String(content, StandardCharsets.UTF_8);
                requestLog.append("Body:\n").append(body).append("\n");
            }
        }

        requestLog.append("========================\n");
        logger.info(requestLog.toString());
    }

    private void logResponse(ContentCachingResponseWrapper response, long duration) {
        StringBuilder responseLog = new StringBuilder();
        responseLog.append("\n=== OUTGOING RESPONSE ===\n");
        responseLog.append("Status: ").append(response.getStatus()).append("\n");
        responseLog.append("Duration: ").append(duration).append("ms\n");

        // Log response headers
        responseLog.append("Headers:\n");
        response.getHeaderNames().forEach(headerName ->
                responseLog.append("  ").append(headerName).append(": ")
                        .append(response.getHeader(headerName)).append("\n"));

        // Log response body
        byte[] content = response.getContentAsByteArray();
        if (content.length > 0) {
            String body = new String(content, StandardCharsets.UTF_8);
            responseLog.append("Body:\n").append(body).append("\n");
        }

        responseLog.append("=========================\n");
        logger.info(responseLog.toString());
    }
}