package com.chintanpathak.spring.reactive.playground.util;

import reactor.core.publisher.Mono;
import java.time.Duration;

public class ReactiveUtil {

    // Simulate a delay for demonstrating reactive timeouts, etc.
    public static Mono<String> delayedResponse(String message) {
        return Mono.just(message)
                .delayElement(Duration.ofSeconds(2));  // Simulate delay of 2 seconds
    }
}