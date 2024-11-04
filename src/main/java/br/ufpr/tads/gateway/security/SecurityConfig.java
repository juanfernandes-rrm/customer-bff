package br.ufpr.tads.gateway.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

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

    @Autowired
    private CustomServerLogoutSuccessHandler customLogoutSuccessHandler;

    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http.addFilterBefore(clientTypeFilter, SecurityWebFiltersOrder.HTTP_BASIC);
        http.authorizeExchange(conf -> conf
                .pathMatchers("/login")
                .permitAll()
                .anyExchange()
                .authenticated());
        http.csrf(ServerHttpSecurity.CsrfSpec::disable);
        http.cors(cors -> cors
                .configurationSource(getCorsConfigurationSource()));
        http.oauth2Login(conf -> conf
                .authorizationRequestResolver(customServerOAuth2AuthorizationRequestResolver)
                .authenticationSuccessHandler(customServerAuthenticationSuccessHandler));
        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessHandler(customLogoutSuccessHandler));
        return http.build();
    }

    private static CorsConfigurationSource getCorsConfigurationSource() {
        return request -> {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:3001"));
            configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            configuration.setAllowedHeaders(List.of("*"));
            return configuration;
        };
    }

}
