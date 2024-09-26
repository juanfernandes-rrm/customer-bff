package br.ufpr.tads.customerbff.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestCustomizers;
import org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Autowired
    private ReactiveClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private ReactiveOAuth2AuthorizedClientService authorizedClientService;

    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http.authorizeExchange(conf -> conf
                .pathMatchers("/login")
                .permitAll()
                .anyExchange()
                .authenticated());
        http.csrf(ServerHttpSecurity.CsrfSpec::disable);
        http.cors(ServerHttpSecurity.CorsSpec::disable);
        http.oauth2Login(conf -> conf
                .authorizationRequestResolver(authorizationRequestResolver())
                .authenticationSuccessHandler(getServerAuthenticationSuccessHandler()));
        return http.build();
    }

    private org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler getServerAuthenticationSuccessHandler() {
        return (webFilterExchange, authentication) -> {
            if (authentication instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;

                return authorizedClientService.loadAuthorizedClient(
                                oauthToken.getAuthorizedClientRegistrationId(), oauthToken.getName())
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
        };
    }

    private ServerOAuth2AuthorizationRequestResolver authorizationRequestResolver() {
        var authorizationRequestResolver = new DefaultServerOAuth2AuthorizationRequestResolver(
                clientRegistrationRepository);
        authorizationRequestResolver.setAuthorizationRequestCustomizer(
                OAuth2AuthorizationRequestCustomizers.withPkce());
        return authorizationRequestResolver;
    }

}
