package br.ufpr.tads.gateway.security;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.server.ServerRedirectStrategy;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

public class CustomServerRedirectStrategy implements ServerRedirectStrategy {

    @Override
    public Mono<Void> sendRedirect(ServerWebExchange exchange, URI location) {
        String responseJson = "{\"logoutUrl\": \"" + location.toString() + "\"}";

        DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
        DataBuffer dataBuffer = bufferFactory.wrap(responseJson.getBytes());

        exchange.getResponse().setStatusCode(HttpStatus.OK);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        return exchange.getResponse().writeWith(Mono.just(dataBuffer));
    }
}
