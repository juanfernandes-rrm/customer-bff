package br.ufpr.tads.gateway.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ClientTypeFilter implements WebFilter {

    private static final String DEFAULT_CLIENT_TYPE = "web";
    private static final String CLIENT_TYPE_PARAM = "clientType";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String clientType = exchange.getRequest().getQueryParams().getFirst(CLIENT_TYPE_PARAM);

        if (clientType == null || clientType.isEmpty()) {
            clientType = DEFAULT_CLIENT_TYPE;
        }
        log.info("Client type: {}", clientType);

        String finalClientType = clientType;
        exchange.getAttributes().put(CLIENT_TYPE_PARAM, finalClientType);

        return chain.filter(exchange);
    }
}
