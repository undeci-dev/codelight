package com.project.codelight.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.codelight.auth.repository.RefreshTokenRepository;
import com.project.codelight.auth.repository.TokenBlackListRepository;
import com.project.codelight.auth.security.filter.CustomAuthenticationFilter;
import com.project.codelight.auth.security.filter.JwtAuthorizationFilter;
import com.project.codelight.auth.security.handler.CustomAuthFailureHandler;
import com.project.codelight.auth.security.handler.CustomAuthSuccessHandler;
import com.project.codelight.auth.security.handler.CustomAuthenticationProvider;
import com.project.codelight.auth.security.oauth2.CustomOAuth2UserService;
import com.project.codelight.auth.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.project.codelight.auth.security.oauth2.OAuth2AuthenticationFailureHandler;
import com.project.codelight.auth.security.oauth2.OAuth2AuthenticationSuccessHandler;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final ObjectMapper objectMapper;
    private final UserDetailsService userDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenBlackListRepository tokenBlackListRepository;
    private final CorsProperties corsProperties;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
            .httpBasic(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/**").permitAll()
                .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .authorizationEndpoint(authorization -> authorization
                    .authorizationRequestRepository(httpCookieOAuth2AuthorizationRequestRepository)
                )
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService)
                )
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticationFailureHandler)
            )
            .addFilterBefore(jwtAuthorizationFilter(),
                BasicAuthenticationFilter.class)
            .addFilterBefore(customAuthenticationFilter(),
                UsernamePasswordAuthenticationFilter.class)
            .formLogin(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable)
            .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(corsProperties.getAllowedOrigins());
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(customAuthenticationProvider());
    }

    @Bean
    public CustomAuthenticationProvider customAuthenticationProvider() {
        return new CustomAuthenticationProvider(userDetailsService, bCryptPasswordEncoder());
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CustomAuthenticationFilter customAuthenticationFilter() {
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(
            authenticationManager(), objectMapper);
        customAuthenticationFilter.setFilterProcessesUrl("/api/local-auth/login");
        customAuthenticationFilter.setAuthenticationSuccessHandler(
            customLoginSuccessHandler());
        customAuthenticationFilter.setAuthenticationFailureHandler(
            customLoginFailureHandler());
        customAuthenticationFilter.afterPropertiesSet();
        return customAuthenticationFilter;
    }

    @Bean
    public CustomAuthSuccessHandler customLoginSuccessHandler() {
        return new CustomAuthSuccessHandler(objectMapper, refreshTokenRepository);
    }

    @Bean
    public CustomAuthFailureHandler customLoginFailureHandler() {
        return new CustomAuthFailureHandler(objectMapper);
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(tokenBlackListRepository, objectMapper);
    }


}