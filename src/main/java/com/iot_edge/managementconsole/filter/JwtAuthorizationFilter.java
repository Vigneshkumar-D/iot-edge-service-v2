//
package com.iot_edge.managementconsole.filter;
//
//import com.iot_edge.managementconsole.repository.authentication.TokenBlacklistRepository;
//import com.iot_edge.managementconsole.service.user.UserService;
//
//import com.iot_edge.managementconsole.utils.user.JwtUtil;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationContext;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.List;
//
//@Component
//public class JwtFilter extends OncePerRequestFilter {
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    @Autowired
//    private ApplicationContext context;
//
//    @Autowired
//    private TokenBlacklistRepository tokenBlacklistRepository;
//    // Define a list of public URLs that should bypass JWT filter
//    private static final List<String> PUBLIC_URLS = Arrays.asList(
//            "/api/auth/login",            // Login endpoint
//            "/api/auth/forget-password",
//            "/api/register",         // Registration endpoint
//            "/swagger-ui/**",        // Swagger UI
//            "/swagger-resources/**", // Swagger resources
//            "/v3/api-docs/**"        // API docs
//    );
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//
//        String requestUri = request.getRequestURI();
//
//        // Skip JWT filtering for public URLs
//        if (isPublicUrl(requestUri)) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        String authHeader = request.getHeader("Authorization");
//        String token = null;
//        String username = null;
//
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            token = authHeader.substring(7); // Extract the token
//
//            Boolean tokenBlacklistService = tokenBlacklistRepository.existsByToken(token);
//            if (tokenBlacklistService) {
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                response.getWriter().write("Token is blacklisted. Access denied.");
//                return;
//            }
//
//            username = jwtUtil.extractUserName(token); // Extract username from token
//        }
//
//        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            // Lazily load UserService
//            UserService userService = context.getBean(UserService.class);
//            UserDetails userDetails = userService.loadUserByUsername(username);
//
//            if (jwtUtil.validateToken(token, userDetails)) {
//                UsernamePasswordAuthenticationToken authToken =
//                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authToken);
//            }
//        }
//
//        filterChain.doFilter(request, response);
//    }
//
//    // Helper method to check if the URL is in the list of public URLs
//    private boolean isPublicUrl(String requestUri) {
//        return PUBLIC_URLS.stream().anyMatch(requestUri::startsWith);
//    }
//}
//


import com.iot_edge.managementconsole.entity.user.User;
import com.iot_edge.managementconsole.repository.user.UserRepository;
import com.iot_edge.managementconsole.service.authentication.AuthenticationService;
import com.iot_edge.managementconsole.utils.user.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Value("${custom.profile.active}")
    private String activeEnvironment;

    private final JwtUtil jwtUtil;

    private AuthenticationService authenticationService;

    private final UserRepository userRepository;

    public JwtAuthorizationFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Extract JWT token from cookie
        String path = request.getServletPath();
        if ("/api/auth/renew-token".equals(path)) {
            filterChain.doFilter(request, response);
            return;
        }
        String jwtToken = extractJwtFromRequest(request);
        String refreshToken = jwtUtil.getRefreshTokenFromCookies(request);
        try {
            if (jwtToken != null) {
                Authentication auth = jwtUtil.getAuthentication(jwtToken);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else if (refreshToken != null) {
                log.info("Access Token has expired... Trying to refresh token...");
                if (authenticationService.isValidRefreshToken(refreshToken)) {
                    String userUuid = authenticationService.getSubjectFromRefreshToken(refreshToken);
                    Optional<User> user = userRepository.findByUuid(UUID.fromString(userUuid));
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userUuid, null, List.of());

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                    Map<String, Object> jwtResponse = authenticationService.generateAuthenticationToken(user.get().getEmail());
                    Map<String, Object> jwtResponseForRefreshToken = authenticationService.generateRefreshToken(user.get().getEmail());
                    String refreshedAccessToken = jwtResponse.get("token").toString();
                    String refreshedRefreshToken = jwtResponseForRefreshToken.get("refreshToken").toString();
                    authenticationService.addCookie(refreshedAccessToken, refreshedRefreshToken, response);
                    log.info("Refresh & Access Tokens Generated >>>>>>>>>");
                    Authentication auth = jwtUtil.getAuthentication(jwtResponse.get("token").toString());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        } catch (Exception e) {
            log.info("Invalid Token : {}", e.getMessage());
            authenticationService.removeCookie(response);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
            return;
        }
        filterChain.doFilter(request, response);
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("AUTH_TOKEN")) {
                    return cookie.getValue();
                }
            }
        } else {
            String authorizationHeader = request.getHeader("Authorization");
            if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
                return authorizationHeader.substring(7);
            }
        }
        return null;
    }
}

