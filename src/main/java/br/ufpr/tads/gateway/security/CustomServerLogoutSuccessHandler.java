package br.ufpr.tads.gateway.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class CustomServerLogoutSuccessHandler implements ServerLogoutSuccessHandler {


    @Value("${spring.security.oauth2.client.provider.keycloak.logout-uri}")
    private String keycloakLogoutUri;

    private static final String CLIENT_TYPE_PARAM = "clientType";
    private static final String DEFAULT_CLIENT_TYPE = "web";
    private static final String CLIENT_TYPE_MOBILE = "mobile";
    private static final String LOGOUT_REDIRECT_URI_FOR_MOBILE = "http://localhost:3001";
    private static final String LOGOUT_REDIRECT_URI_FOR_WEB = "http://localhost:3000";

    private final CustomServerRedirectStrategy  redirectStrategy = new CustomServerRedirectStrategy ();

    @Override
    public Mono<Void> onLogoutSuccess(WebFilterExchange exchange, Authentication authentication) {
        String clientType = getClientType(exchange);

        String postLogoutRedirectUri = CLIENT_TYPE_MOBILE.equals(clientType)
                ? LOGOUT_REDIRECT_URI_FOR_MOBILE
                : LOGOUT_REDIRECT_URI_FOR_WEB;

        String idToken = getIdToken(authentication);

        URI logoutUri = UriComponentsBuilder.fromUriString(keycloakLogoutUri)
                .queryParam("id_token_hint", idToken)
                .queryParam("post_logout_redirect_uri", postLogoutRedirectUri)
                .build()
                .toUri();

        return redirectStrategy.sendRedirect(exchange.getExchange(), logoutUri);
    }

    private static String getClientType(WebFilterExchange exchange) {
        String clientType = exchange.getExchange().getRequest().getQueryParams().getFirst(CLIENT_TYPE_PARAM);
        return (clientType != null && !clientType.isEmpty()) ? clientType : DEFAULT_CLIENT_TYPE;
    }

    private String getIdToken(Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            OidcUser oidcUser = (OidcUser) oauthToken.getPrincipal();
            return oidcUser.getIdToken().getTokenValue();
        }
        return null;
    }
}
