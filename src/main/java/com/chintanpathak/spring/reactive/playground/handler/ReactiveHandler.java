package com.chintanpathak.spring.reactive.playground.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Component
public class ReactiveHandler {

    private static final Logger logger = LoggerFactory.getLogger(ReactiveHandler.class);

    // Mono concept - representing a single or empty value
    public Mono<String> monoExample() {
        logger.info("Handling Mono example");
        return Mono.just("Mono: Hello, this is a single item")
                .doOnTerminate(() -> logger.info("Mono operation completed"));
    }

    // Flux concept - representing a stream of values (0 to N items)
    public Flux<String> fluxExample() {
        logger.info("Handling Flux example");
        return Flux.just("Flux: Item 1", "Flux: Item 2", "Flux: Item 3")
                .doOnTerminate(() -> logger.info("Flux operation completed"));
    }

    // Backpressure concept - demonstrating how we can limit the rate of data flow
    public Flux<Integer> backpressureExample() {
        logger.info("Handling Backpressure example");
        return Flux.range(1, 1000)
                .log()
                .doOnTerminate(() -> logger.info("Backpressure operation completed"))
                .limitRate(10);  // limit the rate of emissions to 10 at a time
    }

    // Simulate parallel processing
    public Flux<String> parallelExample() {
        logger.info("Handling Parallel processing example");
        return Flux.range(1, 5)
                .doOnNext(i -> logger.info("Processing item: " + i))
                .publishOn(Schedulers.parallel())  // Processing in parallel
                .map(i -> "Parallel Item: " + i);
    }
}