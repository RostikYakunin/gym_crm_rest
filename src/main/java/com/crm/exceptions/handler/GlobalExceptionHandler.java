package com.crm.exceptions.handler;

import com.crm.exceptions.PasswordNotMatchException;
import com.crm.exceptions.UserNameChangedException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.postgresql.util.PSQLException;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        var transactionId = MDC.get("transactionId");

        log.error("[{}] Unexpected error: {}", transactionId, ex.getMessage(), ex);
        return ResponseEntity.internalServerError()
                .body("An unexpected error occurred. Please contact support.\n" + ex.getCause());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException ex) {
        var transactionId = MDC.get("transactionId");
        log.warn("[{}] Entity not found: {}", transactionId, ex.getMessage());

        return ResponseEntity.ok()
                .body("Entity was deleted");
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handleBadRequestException(BadRequestException ex) {
        var transactionId = MDC.get("transactionId");
        log.warn("[{}] Bad request: {}", transactionId, ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        var transactionId = MDC.get("transactionId");
        var errors = new HashMap<String, String>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(
                        error -> errors.put(error.getField(), error.getDefaultMessage())
                );

        log.warn("[{}] Validation error: {}", transactionId, errors);
        return ResponseEntity.badRequest()
                .body(errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
        var transactionId = MDC.get("transactionId");
        var errors = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage,
                        (existing, replacement) -> existing
                ));

        log.warn("[{}] Constraint violation: {}", transactionId, errors);
        return ResponseEntity.badRequest()
                .body(errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleInvalidRequestBody(HttpMessageNotReadableException ex) {
        var transactionId = MDC.get("transactionId");

        log.warn("[{}] Malformed request body: {}", transactionId, ex.getMessage());
        return ResponseEntity.badRequest()
                .body("Invalid request body: " + ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex) {
        var transactionId = MDC.get("transactionId");

        log.warn("[{}] Access denied: {}", transactionId, ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Access denied: " + ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException ex) {
        var transactionId = MDC.get("transactionId");

        log.warn("[{}] Registration conflict: {}", transactionId, ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ex.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<String> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        var transactionId = MDC.get("transactionId");

        log.warn("[{}] This request with these parameters is not supported: {}", transactionId, ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ex.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<String> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        var transactionId = MDC.get("transactionId");

        log.warn("[{}] Parameters are not correct for this request: {}", transactionId, ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ex.getMessage());
    }

    @ExceptionHandler(PSQLException.class)
    public ResponseEntity<String> handlePSQLException(PSQLException ex) {
        var transactionId = MDC.get("transactionId");

        log.warn("[{}] Something went wrong with SQL request: {}", transactionId, ex.getMessage());
        return ResponseEntity.badRequest()
                .body("Your request is wrong, please pay attention on your request details");
    }

    @ExceptionHandler(PasswordNotMatchException.class)
    public ResponseEntity<String> handlePasswordNotMatchException(PasswordNotMatchException ex) {
        var transactionId = MDC.get("transactionId");

        log.warn("[{}] Password was not changed: inputted password is wrong: {}", transactionId, ex.getMessage());
        return ResponseEntity.badRequest()
                .body("Password was not changed:" + ex.getMessage());
    }

    @ExceptionHandler(UserNameChangedException.class)
    public ResponseEntity<String> handleUserNameChangedException(UserNameChangedException ex) {
        var transactionId = MDC.get("transactionId");

        log.warn("[{}] User name can not be changed: {}", transactionId, ex.getMessage());
        return ResponseEntity.badRequest()
                .body("User name was changed:" + ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        var transactionId = MDC.get("transactionId");

        log.warn("[{}] Arguments are wrong: {}", transactionId, ex.getMessage());
        return ResponseEntity.badRequest()
                .body("Arguments are wrong:" + ex.getMessage());
    }
}
