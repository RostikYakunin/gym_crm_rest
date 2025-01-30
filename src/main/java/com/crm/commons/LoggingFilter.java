package com.crm.commons;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
public class LoggingFilter extends OncePerRequestFilter {
    private static final String TRANSACTION_ID = "transactionId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        var transactionId = (String) request.getAttribute(TRANSACTION_ID);
        if (transactionId == null) {
            transactionId = UUID.randomUUID().toString();
            request.setAttribute(TRANSACTION_ID, transactionId);
        }

        MDC.put(TRANSACTION_ID, transactionId);
        log.info("[{}] Incoming request: {} {}", transactionId, request.getMethod(), request.getRequestURI());
        filterChain.doFilter(request, response);

        log.info("[{}] Response: {} {}\n", transactionId, response.getStatus(), response.getContentType());
        MDC.remove(TRANSACTION_ID);
    }
}
