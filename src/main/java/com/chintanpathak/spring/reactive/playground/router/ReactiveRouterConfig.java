package com.chintanpathak.spring.reactive.playground.router;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import com.chintanpathak.spring.reactive.playground.handler.ReactiveHandler;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import reactor.core.publisher.Mono;

@Configuration
public class ReactiveRouterConfig {

    private final ReactiveHandler reactiveHandler;

    public ReactiveRouterConfig(ReactiveHandler reactiveHandler) {
        this.reactiveHandler = reactiveHandler;
    }

    @Bean
    public RouterFunction<ServerResponse> route() {
        return RouterFunctions.route()
                .GET("/mono", request ->
                        reactiveHandler
                                .monoExample()
                                .flatMap(body -> ServerResponse.ok().bodyValue(body)))
                .GET("/flux", request ->
                        reactiveHandler
                                .fluxExample()
                                .collectList()
                                .flatMap(body -> ServerResponse.ok().bodyValue(body)))
                .GET("/backpressure", request ->
                        reactiveHandler
                                .backpressureExample()
                                .collectList().flatMap(body -> ServerResponse.ok().bodyValue(body)))
                .GET("/parallel", request ->
                        reactiveHandler
                                .parallelExample()
                                .collectList()
                                .flatMap(body -> ServerResponse.ok().bodyValue(body)))
                .GET("/mono-error", request ->
                        reactiveHandler
                                .monoExampleWithError()
                                .flatMap(body -> ServerResponse.ok().bodyValue(body)))
                .GET("/flux-error", request ->
                        reactiveHandler
                                .fluxExampleWithError()
                                .collectList()
                                .flatMap(body -> ServerResponse.ok().bodyValue(body)))
                .GET("/trigger-global-error", request ->
                        reactiveHandler
                                .triggerGlobalException()
                                .flatMap(body -> ServerResponse.ok().bodyValue(body)))
                .GET("/mono-with-cache", request ->
                        reactiveHandler
                                .monoExampleWithCache()
                                .flatMap(body -> ServerResponse.ok().bodyValue(body)))
                .GET("/flux-error-with-fallback", request ->
                        reactiveHandler
                                .fluxExampleWithFallback()
                                .flatMap(body -> ServerResponse.ok().bodyValue(body)))
                .GET("/mono-map", request ->
                        reactiveHandler
                                .monoExampleWithMap()
                                .flatMap(body -> ServerResponse.ok().bodyValue(body)))
                .GET("/mono-flatmap", request ->
                        reactiveHandler
                                .monoExampleWithFlatMap()
                                .flatMap(body -> ServerResponse.ok().bodyValue(body)))
                .GET("/mono-side-effects", request ->
                        reactiveHandler
                                .monoExampleWithSideEffects()
                                .flatMap(body -> ServerResponse.ok().bodyValue(body)))
                .GET("/mono-zip", request ->
                        reactiveHandler
                                .monoZipExample()
                                .flatMap(body -> ServerResponse.ok().bodyValue(body)))
                .GET("/flux-zip", request ->
                        reactiveHandler
                                .fluxZipExample()
                                .collectList()
                                .flatMap(body -> ServerResponse.ok().bodyValue(body)))
                .GET("/parallel-flux", request ->
                        reactiveHandler
                                .parallelFluxExample()
                                .collectList()
                                .flatMap(body -> ServerResponse.ok().bodyValue(body)))
                .POST("/upload", this::handleFileUpload)
                .build();
    }

    private Mono<ServerResponse> handleFileUpload(ServerRequest request) {
        return request.multipartData()
                .flatMap(formData -> {
                    // Get the file from the multipart data
                    var file = formData.toSingleValueMap().get("file");
                    if (file != null) {
                        return reactiveHandler
                                .handleFileUpload(file)
                                .flatMap(responseMessage -> ServerResponse.ok().bodyValue(responseMessage));
                    } else {
                        return ServerResponse.badRequest().bodyValue("No file provided");
                    }
                });
    }
}
