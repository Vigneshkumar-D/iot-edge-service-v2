package com.iot_edge.managementconsole.utils.user;

import com.iot_edge.managementconsole.entity.user.User;
import com.iot_edge.managementconsole.repository.user.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private final JwtDecoder jwtDecoder;
    private final UserRepository userRepository;

    public JwtUtil(JwtDecoder jwtDecoder, UserRepository userRepository) {
        this.jwtDecoder = jwtDecoder;
        this.userRepository = userRepository;
    }

    public boolean checkRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

            return authorities.stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(role));
        }

        throw new IllegalStateException("User not authenticated or authorities are not loaded.");
    }

    public Authentication getAuthentication(String jwtToken) {
        Jwt decodedJwt = jwtDecoder.decode(jwtToken);
        String sub = decodedJwt.getClaimAsString("sub");
        String name = decodedJwt.getClaimAsString("name");
        String email = decodedJwt.getClaimAsString("email");
        String contactNo = decodedJwt.getClaimAsString("contactNo");

        Map<String, Object> organizationMap = decodedJwt.getClaim("organization");
        String organizationUuid = null;
//        if (organizationMap != null) {
//            ObjectMapper objectMapper = new ObjectMapper();
//            OrganizationForTokenResponseDTO organizationForTokenResponseDTO;
//            organizationForTokenResponseDTO = objectMapper.convertValue(organizationMap, OrganizationForTokenResponseDTO.class);
//            organizationUuid = organizationForTokenResponseDTO.getUuid().toString();
//        }

        Collection<SimpleGrantedAuthority> authorities =
                decodedJwt.getClaimAsStringList("role").stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(sub, null, authorities);
        authenticationToken.setDetails(new AuthenticationDetails(
                name, email, contactNo, sub,
                authorities.stream().map(
                        SimpleGrantedAuthority::getAuthority).collect(
                        Collectors.joining(",")), null));
        return authenticationToken;
    }

    public User extractJwtUserFromRequest(HttpServletRequest request) {
        String token = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("AUTH_TOKEN")) {
                    token = cookie.getValue();
                }
            }
        } else {
            String authorizationHeader = request.getHeader("Authorization");
            if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
                token = authorizationHeader.substring(7);
            }
        }

        Jwt decodedToken = jwtDecoder.decode(token);
        String userUUID = decodedToken.getClaims().get("sub").toString();

        return userRepository.findByUuid(UUID.fromString(userUUID)).get();
    }

    public AuthenticationDetails getAuthenticationDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            return (AuthenticationDetails) authentication.getDetails();
        }

        throw new IllegalStateException("User not authenticated or authorities are not loaded.");
    }

    public boolean isUserPresent(UUID userUuid) {
        return userRepository.existsByUuid(userUuid);
    }

    public String getRefreshTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("REFRESH_TOKEN".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public String getAccessTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("AUTH_TOKEN".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
