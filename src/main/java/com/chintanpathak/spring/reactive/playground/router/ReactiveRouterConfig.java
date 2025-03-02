package com.chintanpathak.spring.reactive.playground.router;
import org.springframework.web.reactive.function.server.ServerResponse;
import com.chintanpathak.spring.reactive.playground.handler.ReactiveHandler;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;

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
                .build();
    }
}
