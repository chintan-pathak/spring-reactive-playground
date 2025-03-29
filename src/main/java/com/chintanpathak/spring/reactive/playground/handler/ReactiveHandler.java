package com.chintanpathak.spring.reactive.playground.handler;

import com.chintanpathak.spring.reactive.playground.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class ReactiveHandler {

    private static final Logger logger = LoggerFactory.getLogger(ReactiveHandler.class);

    private static Mono<String> cachedValue;

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
                .doOnNext(i -> logger.info("Processing item: {}", i))
                .publishOn(Schedulers.parallel())  // Processing in parallel
                .map(i -> "Parallel Item: " + i);
    }

    // Simulating an exception being thrown in a reactive stream
    public Mono<Object> monoExampleWithError() {
        logger.info("Handling mono example with error");
        return Mono.error(new ResourceNotFoundException("Custom Resource Not Found"))
                .onErrorResume(ex -> {
                    // Log the exception (you can use SLF4J here for better logging)
                    logger.error("Error occurred: {}", ex.getMessage());
                    // Handle the exception and provide a fallback value
                    return Mono.just("Fallback Value: Resource not found");
                })
                .doOnTerminate(() -> logger.info("Mono operation completed with error handling"));
    }

    // Flux example with exception handling
    public Flux<String> fluxExampleWithError() {
        logger.info("Handling flux example with error");
        return Flux.just("Item 1", "Item 2", "Item 3")
                .concatWith(Mono.error(new ResourceNotFoundException("Custom Resource Not Found")))
                .onErrorResume(ex -> {
                    logger.error("Error occurred: {}", ex.getMessage());
                    return Flux.just("Fallback Item 1", "Fallback Item 2");
                })
                .doOnTerminate(() -> logger.info("Flux operation completed with error handling"));
    }

    // Endpoint that triggers the global exception handler
    public Mono<String> triggerGlobalException() {
        logger.info("Handling global exception example");
        // Here, we're intentionally throwing an exception to trigger the global exception handler
        return Mono.error(new RuntimeException("This is a test exception to trigger the global handler."));
    }

    // Endpoint that demonstrates the usage of .cache()
    public Mono<String> monoExampleWithCache() {
        if (cachedValue == null) {
            // Expensive operation that we want to cache the result of.
            cachedValue = Mono.just("This is a cached value!")
                    .doOnTerminate(() -> logger.info("Cache has been populated!"))
                    .cache(); // Cache the value
        }
        return cachedValue;
    }

    // Example method using onErrorResume to provide fallback on error
    public Mono<Object> fluxExampleWithFallback() {
        return Mono.just("Starting stream")
                .flatMap(_ -> Mono.error(new RuntimeException("Something went wrong!")))
                .onErrorResume(e -> {
                    logger.error("Error occurred: {}", e.getMessage());
                    return Mono.just("Fallback value in case of error");
                });
    }

    // Example using .map() to transform values
    public Mono<String> monoExampleWithMap() {
        return Mono.just("Original Value")
                .map(value -> value + " - Transformed Value using map()");
    }

    // Example using .flatMap() for chaining async operations
    public Mono<String> monoExampleWithFlatMap() {
        return Mono.just("Initial Data")
                .flatMap(data -> {
                    // Simulate an asynchronous operation, such as a database call
                    return Mono.just(data + " - Processed Asynchronously");
                });
    }

    // Example using side effects with doOnNext, doOnError, and doOnTerminate
    public Mono<String> monoExampleWithSideEffects() {
        return Mono.just("Value for side effects")
                .doOnNext(value -> logger.info("Received value: {}", value))
                .doOnError(error -> logger.error("Error encountered: {}", error.getMessage()))
                .doOnTerminate(() -> logger.info("Stream has completed!"));
    }

    // Combine two Monos using .zip()
    public Mono<String> monoZipExample() {
        Mono<String> firstMono = Mono.just("First Value");
        Mono<String> secondMono = Mono.just("Second Value");

        // Using .zip() to combine both Monos
        return Mono.zip(firstMono, secondMono, (first, second) -> {
            // Combine the values from both Monos
            return first + " + " + second;
        });
    }

    // Combine two Flux using .zip()
    public Flux<String> fluxZipExample() {
        Flux<String> firstFlux = Flux.just("A", "B", "C");
        Flux<String> secondFlux = Flux.just("1", "2", "3");

        // Using .zip() to combine both Flux
        return Flux.zip(firstFlux, secondFlux, (first, second) -> {
            // Combine elements from both Fluxes
            return first + second;
        });
    }

    // Parallel Flux Example - Processing items in parallel
    public Flux<String> parallelFluxExample() {
        // Create a Flux with multiple items
        Flux<String> itemsFlux = Flux.just("Item1", "Item2", "Item3", "Item4");

        // Process each item in parallel and add a simulated delay for each item
        return itemsFlux
                .parallel()  // Enable parallel processing
                .runOn(ioScheduler())  // Specify the scheduler for parallel execution (e.g., IO scheduler)
                .map(this::processItem)  // Process each item in parallel
                .sequential();  // Merge the results back into a sequential Flux
    }

    // Handler method for processing the uploaded file
    public Mono<String> handleFileUpload(Part file) {
        return Mono.fromRunnable(() -> {
                    try {
                        // Process the file (for demonstration, we'll just print its name and size)
                        logger.info("Received file: " + file.name());
                        logger.info("File size: " + file.headers().getContentLength() + " bytes");

                        // Optionally save or process the file further, e.g., store it in a database, etc.
                        // For simplicity, we'll assume the file is processed here.

                        // The file can also be saved to disk using file.getInputStream(), or in a database, etc.
                    }
                    catch (Exception e) {
                        throw new RuntimeException("Error processing file upload", e);
                    }
                })
                .then(Mono.just("File uploaded successfully!")); // Return success message
    }

    // Simulate processing of an item with a delay
    private String processItem(String item) {
        try {
            Thread.sleep(500);  // Simulate processing time
        } catch (InterruptedException e) {
            logger.error("Error occurred: {}", e.getMessage());
        }
        return item + " processed";
    }

    // Example of using a specific scheduler for parallel execution (like IO)
    private reactor.core.scheduler.Scheduler ioScheduler() {
        return reactor.core.scheduler.Schedulers.boundedElastic();
    }
}