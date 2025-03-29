package com.chintanpathak.spring.reactive.playground.spring;

import com.chintanpathak.spring.reactive.playground.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<String>> handleGeneralException(Exception ex) {
        // Log the exception (you can use a logger here)
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal Server Error: " + ex.getMessage()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public Mono<ResponseEntity<String>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Resource not found: " + ex.getMessage()));
    }

    // Handle all RuntimeException globally
    @ExceptionHandler(RuntimeException.class)
    public Mono<ResponseEntity<String>> handleRuntimeException(RuntimeException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Runtime Exception: " + ex.getMessage()));
    }
}
