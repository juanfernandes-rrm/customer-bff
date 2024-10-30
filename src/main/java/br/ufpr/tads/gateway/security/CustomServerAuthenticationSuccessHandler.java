package br.ufpr.tads.gateway.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class CustomServerAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    @Autowired
    private ReactiveOAuth2AuthorizedClientService authorizedClientService;

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;

            return authorizedClientService.loadAuthorizedClient(oauthToken.getAuthorizedClientRegistrationId(), oauthToken.getName())
                .flatMap(authorizedClient -> {
                    String accessToken = authorizedClient.getAccessToken().getTokenValue();

                    String responseBody = "{\"token\":\"" + accessToken + "\"}";

                    ServerWebExchange exchange = webFilterExchange.getExchange();
                    DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
                    DataBuffer dataBuffer = bufferFactory.wrap(responseBody.getBytes());

                    exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                    exchange.getResponse().setStatusCode(HttpStatus.OK);

                    return exchange.getResponse().writeWith(Mono.just(dataBuffer));
                });
        }
        return Mono.empty();
    }
}
