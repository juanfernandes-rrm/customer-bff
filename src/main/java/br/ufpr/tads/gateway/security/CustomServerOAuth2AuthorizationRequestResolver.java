package br.ufpr.tads.gateway.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class CustomServerOAuth2AuthorizationRequestResolver implements ServerOAuth2AuthorizationRequestResolver {

    private static final String DEFAULT_CLIENT_TYPE = "web";
    private static final String CLIENT_TYPE_PARAM = "clientType";
    public static final String CLIENT_TYPE_MOBILE = "mobile";
    public static final String REDIRECT_URI_FOR_MOBILE = "http://localhost:3001/callback";
    public static final String REDIRECT_URI_FOR_WEB = "http://localhost:3000/callback";

    private final DefaultServerOAuth2AuthorizationRequestResolver defaultResolver;

    public CustomServerOAuth2AuthorizationRequestResolver(ReactiveClientRegistrationRepository clientRegistrationRepository) {
        this.defaultResolver = new DefaultServerOAuth2AuthorizationRequestResolver(clientRegistrationRepository);
    }

    @Override
    public Mono<OAuth2AuthorizationRequest> resolve(ServerWebExchange exchange) {
        return this.defaultResolver.resolve(exchange)
                .map(authorizationRequest -> customizeAuthorizationRequest(authorizationRequest, getClientType(exchange)));
    }

    @Override
    public Mono<OAuth2AuthorizationRequest> resolve(ServerWebExchange exchange, String clientRegistrationId) {
        return this.defaultResolver.resolve(exchange, clientRegistrationId)
                .map(authorizationRequest -> customizeAuthorizationRequest(authorizationRequest, getClientType(exchange)));
    }

    private static String getClientType(ServerWebExchange exchange) {
        String clientType = exchange.getRequest().getQueryParams().getFirst(CLIENT_TYPE_PARAM);
        if (clientType == null || clientType.isEmpty()) {
            clientType = DEFAULT_CLIENT_TYPE;
        }
        log.info("Client type: {}", clientType);
        return clientType;
    }

    private OAuth2AuthorizationRequest customizeAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, String clientType) {
        OAuth2AuthorizationRequest.Builder builder = OAuth2AuthorizationRequest.from(authorizationRequest);

        if (CLIENT_TYPE_MOBILE.equals(clientType)) {
            builder.redirectUri(REDIRECT_URI_FOR_MOBILE);
        } else {
            builder.redirectUri(REDIRECT_URI_FOR_WEB);
        }

        return builder.build();
    }
}

