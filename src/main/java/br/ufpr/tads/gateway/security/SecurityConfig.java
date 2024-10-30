package br.ufpr.tads.gateway.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Autowired
    private ReactiveClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private CustomServerOAuth2AuthorizationRequestResolver customServerOAuth2AuthorizationRequestResolver;

    @Autowired
    private CustomServerAuthenticationSuccessHandler customServerAuthenticationSuccessHandler;

    @Autowired
    ClientTypeFilter clientTypeFilter;

    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http.addFilterBefore(clientTypeFilter, SecurityWebFiltersOrder.HTTP_BASIC);
        http.authorizeExchange(conf -> conf
                .pathMatchers("/login")
                .permitAll()
                .anyExchange()
                .authenticated());
        http.csrf(ServerHttpSecurity.CsrfSpec::disable);
        http.cors(ServerHttpSecurity.CorsSpec::disable);
        http.oauth2Login(conf -> conf
                .authorizationRequestResolver(customServerOAuth2AuthorizationRequestResolver)
                .authenticationSuccessHandler(customServerAuthenticationSuccessHandler));
        return http.build();
    }

}
