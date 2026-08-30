package com.codesync.common.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(
            HttpSecurity http) throws Exception {

        AuthenticationEntryPoint apiEntryPoint =
                (request, response, exception) -> {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType("application/json");
                    response.getWriter().write("""
                            {
                              "title": "Unauthorized",
                              "status": 401,
                              "detail": "Authentication is required."
                            }
                            """);
                };

        http
                .securityMatcher("/api/**")

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/health")
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                )

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(apiEntryPoint)
                )

                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/v1/health")
                )

                .sessionManagement(session -> session
                        .sessionCreationPolicy(
                                SessionCreationPolicy.IF_REQUIRED
                        )
                );

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain oauthSecurityFilterChain(
            HttpSecurity http,
            GitHubOAuth2UserService gitHubOAuth2UserService)
            throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/oauth2/**",
                                "/login/**"
                        )
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                )

                .sessionManagement(session -> session
                        .sessionCreationPolicy(
                                SessionCreationPolicy.IF_REQUIRED
                        )
                )

                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(
                                        gitHubOAuth2UserService
                                )
                        )
                        .loginPage(
                                "/oauth2/authorization/github"
                        )
                )

                .headers(headers -> headers
                        .frameOptions(frameOptions ->
                                frameOptions.deny()
                        )
                        .contentTypeOptions(contentTypeOptions -> {})
                        .referrerPolicy(referrerPolicy ->
                                referrerPolicy.policy(
                                        org.springframework.security.web.header.writers
                                                .ReferrerPolicyHeaderWriter
                                                .ReferrerPolicy
                                                .STRICT_ORIGIN_WHEN_CROSS_ORIGIN
                                )
                        )
                );

        return http.build();
    }
}