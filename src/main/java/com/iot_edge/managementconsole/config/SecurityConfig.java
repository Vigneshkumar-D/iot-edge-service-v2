package com.iot_edge.managementconsole.config;

import com.iot_edge.managementconsole.filter.JwtAuthorizationFilter;
import com.iot_edge.managementconsole.repository.user.UserRepository;
import com.iot_edge.managementconsole.utils.user.JwtUtil;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableMethodSecurity(jsr250Enabled = true, securedEnabled = true)
@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {
    @Value("${spring.profiles.active}")
    private String activeEnvironment;

    @Value("${jwt.public.key}")
    RSAPublicKey publicKey;

    @Value("${jwt.private.key}")
    RSAPrivateKey privateKey;

    private final JwtUtil jwtUtil;

    private final UserRepository userRepository;

    public SecurityConfig(@Lazy JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .securityMatcher("/api/**", "/swagger-ui/**")
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
//                                "/",
                                "/api/auth/**",
                                "/mqtt/**",
                                "/forget-password",
                                "/reset-password",
                                "/public/**",
                                "/api/ws/**",
                                "/api/otp/**",
                                "/api/crypto/**",
                                "/swagger-ui/**",
                                "/api/v3/api-docs/**", "/swagger-resources/**"
                        ).permitAll()
                        .requestMatchers("/public/**","/api/ws/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
                .httpBasic(withDefaults())  // Enables Basic Auth
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                        .accessDeniedHandler(new BearerTokenAccessDeniedHandler())
                );

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        switch (activeEnvironment) {
            case "dev" -> {
                log.info("Setting allowed origins for DEV environment");
                configuration.setAllowedOrigins(List.of("http://localhost:8080", "http://localhost:3000", "https://iot-edge-dev.sti.com", "https://iot-edge-service.sti.com"));
            }
            case "uat" -> {
                log.info("Setting allowed origins for UAT environment");
                configuration.setAllowedOrigins(List.of("https://iot-edge-uat.sti.com", "https://iot-edge-uat-service.sti.com"));
            }
            case "prod" -> {
                log.info("Setting allowed origins for PROD environment");
                configuration.setAllowedOrigins(List.of("https://iot-edge.sti.com", "https://iot-edge-service.sti.com"));
            }
            default -> {
                log.info("Setting allowed origins for LOCAL environment");
                configuration.setAllowedOrigins(List.of("http://localhost:8080", "http://localhost:3000", "https://cognition-dev.sti.com", "https://cognition-dev-service.sti.com"));
            }
        }
        configuration.setAllowedMethods(List.of("GET", "UPDATE", "PUT", "DELETE", "OPTIONS", "POST"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(this.publicKey).build();
    }

    @Bean
    JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(this.publicKey).privateKey(this.privateKey).build();
        JWKSource<SecurityContext> jwk_source = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwk_source);
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtUtil, userRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
