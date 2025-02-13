package com.crm.commons;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class TransactionLoggingFilter extends OncePerRequestFilter {
    private static final String TRANSACTION_ID = "transactionId";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            var transactionId = request.getHeader(TRANSACTION_ID);
            if (transactionId == null || transactionId.isEmpty()) {
                transactionId = UUID.randomUUID().toString();
            }

            MDC.put(TRANSACTION_ID, transactionId);
            request.setAttribute(TRANSACTION_ID, transactionId);
            response.setHeader(TRANSACTION_ID, transactionId);
            log.info("➡️ Incoming request: [{}] {} | TransactionId: {}",
                    request.getMethod(), request.getRequestURI(), transactionId);

            filterChain.doFilter(request, response);
            log.info("⬅️ Response: [{}] {} | Status: {} | TransactionId: {}",
                    request.getMethod(), request.getRequestURI(), response.getStatus(), transactionId);
        } finally {
            MDC.clear();
        }
    }
}
