package com.iot_edge.managementconsole.service.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot_edge.common.exceptions.*;
import com.iot_edge.managementconsole.dto.request.AuthenticationRequestDTO;
import com.iot_edge.managementconsole.dto.user.FirmForTokenResponseDTO;
import com.iot_edge.managementconsole.entity.system.Firm;
import com.iot_edge.managementconsole.entity.user.User;
import com.iot_edge.managementconsole.repository.user.RoleRepository;
import com.iot_edge.managementconsole.repository.user.UserRepository;
import com.iot_edge.managementconsole.utils.user.JwtUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.*;

@Service
@Slf4j
public class AuthenticationService {

    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    private final UserRepository userRepository;
    private final CryptoService cryptoService;
    private final JwtEncoder encoder;
    private final RoleRepository userRoleRepository;
    private final OTPService otpService;
//    private final ProfileService profileService;
    private final JwtUtil jwtUtil;

    @Value("${custom.secret_access_key}")
    private String SECRET_ACCESS_KEY;

    @Value("${custom.profile.active}")
    private String activeEnvironment;

    @Value("${jwt.expiration:1800}")
    private Long jwtExpiration;

    @Value("${jwt.refreshTokenExpiration:2100}")
    private Long refreshTokenExpiration;

    @Value("${jwt.public.key}")
    private String publicKeyPath;

    private PublicKey publicKey;

    @PostConstruct
    public void init() {
        try {
            publicKey = loadPublicKey(publicKeyPath);
        } catch (Exception e) {
            log.error("Error loading keys", e);
        }
    }

    public AuthenticationService(UserRepository userRepository, CryptoService cryptoService, JwtEncoder encoder, RoleRepository userRoleRepository, @Qualifier("objectMapper") ObjectMapper objectMapper, OTPService otpService, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.cryptoService = cryptoService;
        this.encoder = encoder;
        this.userRoleRepository = userRoleRepository;
        this.otpService = otpService;
//        this.profileService = profileService;
        this.jwtUtil = jwtUtil;
    }

    // Load the public key from the file
    private PublicKey loadPublicKey(String publicKeyPath) throws Exception {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(publicKeyPath.replace("classpath:", ""));
        if (inputStream == null) {
            throw new IllegalArgumentException("Public key file not found: " + publicKeyPath);
        }
        byte[] keyBytes = inputStream.readAllBytes();
        String publicKeyPEM = new String(keyBytes)
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] decodedKey = Base64.getDecoder().decode(publicKeyPEM);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    public boolean isValidRefreshToken(String refreshToken) {
        try {
            // Parse the JWT token to extract claims
            Jwts.parserBuilder()
                    .setSigningKey(publicKey) // Secret key for validation
                    .build()
                    .parseClaimsJws(refreshToken);

            // If parsing is successful, the token is valid (not expired, signed correctly, etc.)
            return true;
        } catch (ExpiredJwtException e) {
            // Token is expired
            System.out.println("Token is expired.");
        } catch (UnsupportedJwtException e) {
            // Unsupported JWT token
            System.out.println("Unsupported JWT token.");
        } catch (MalformedJwtException e) {
            // Malformed JWT token
            System.out.println("Malformed JWT token.");
        } catch (SignatureException e) {
            // Invalid signature
            System.out.println("Invalid JWT signature.");
        } catch (IllegalArgumentException e) {
            // Token is null or empty
            System.out.println("JWT claims string is empty.");
        }

        // If any of the exceptions were caught, the token is invalid
        return false;
    }

    public String verifyCredentials(AuthenticationRequestDTO authenticationRequestDTO, HttpServletResponse response, String secret) throws UnauthorizedException, ChangeSetPersister.NotFoundException, IOException, TooManyRequestsException, NotFoundException {
        Optional<User> user = userRepository.findByEmailIgnoreCase(authenticationRequestDTO.getEmail());
        if (user.isPresent()) {
            if (user.get().getIsEmailVerified() && user.get().getIsActive()) {
                String DECRYPTED_PASSWORD = cryptoService.decrypt(authenticationRequestDTO.getPassword());
                authenticationRequestDTO.setPassword(DECRYPTED_PASSWORD);
                if (passwordEncoder.matches(authenticationRequestDTO.getPassword(), user.get().getPassword())) {
                    log.info("User credentials matches..");
                    if (secret.equals(SECRET_ACCESS_KEY)) {
                        Map<String, Object> jwtResponse = generateAuthenticationToken(authenticationRequestDTO.getEmail());
                        Map<String, Object> jwtResponseForRefreshToken = generateRefreshToken(authenticationRequestDTO.getEmail());
                        addCookie(jwtResponse.get("token").toString(), jwtResponseForRefreshToken.get("refreshToken").toString(), response);
                    } else {
                        Map<String, Object> emailOTPTemplateData = new HashMap<>();
                        otpService.generateOTPForEmail(user.get().getEmail(), emailOTPTemplateData, null, null, 0);
                    }
                } else {
                    throw new UnauthorizedException("Invalid credentials!");
                }
            } else if (!user.get().getIsEmailVerified()) {
                Map<String, Object> emailOTPTemplateData = new HashMap<>();
                int genOTP = OTPService.generateOTP();
                otpService.generateOTPForEmail(user.get().getEmail(), emailOTPTemplateData, null, null, genOTP);
            } else {
                throw new UnauthorizedException("User is not active! Contact Administrator.");
            }
        } else {
            throw new NotFoundException("User not found!");
        }
        if (secret.equals(SECRET_ACCESS_KEY)) {
            return "Logged in with secret access credentials.";
        } else {
            return "OTP has been sent to the registered email address.";
        }
    }

    public Map<String, Object> generateAuthenticationToken(String email) throws ChangeSetPersister.NotFoundException {
        Optional<User> user = userRepository.findByEmailIgnoreCase(email);

        if (user.isPresent()) {
            Map<String, Object> session = new HashMap<>();

            Instant now = Instant.now();

            String userRoles = user.get().getRole().getRoleName();


            boolean isOrganizationAdmin = userRoles.contains("ROLE_SCHOOL_ADMIN") || userRoles.contains("ROLE_UNIVERSITY_ADMIN") || userRoles.contains("ROLE_CORPORATE_ADMIN");

            boolean isEmployee = userRoles.contains("ROLE_EMPLOYEE");

            JwtClaimsSet.Builder builder = JwtClaimsSet.builder()
                    .issuedAt(now)
                    .expiresAt(now.plusSeconds(jwtExpiration))
                    .subject(String.valueOf(user.get().getUuid()))
                    .claim("uuid", String.valueOf(user.get().getUuid()))
//                    .claim("avatarUrl", String.valueOf(user.get().getAvatarUrl()))
                    .claim("name", user.get().getUsername())
                    .claim("role", userRoles);

            builder.claim("email", user.get().getEmail());
            session.put("email", user.get().getEmail());
            if (user.get().getMobileNumber() != null) {
                builder.claim("contactNo", user.get().getMobileNumber());
                session.put("contactNo", user.get().getMobileNumber());
            }

            if (isOrganizationAdmin) {
                Firm firm = user.get().getRole().getFirm();
                FirmForTokenResponseDTO firmForTokenResponseDTO = FirmForTokenResponseDTO.builder()
                        .uuid(firm.getUuid())
                        .logoUrl(firm.getLogoUrl())
                        .name(firm.getName())
                        .email(firm.getEmail())
                        .contactNo(firm.getContactNo())
                        .website(firm.getWebsite())
                        .build();
                builder.claim("firm", firmForTokenResponseDTO);
                session.put("firm", firmForTokenResponseDTO);

//                builder.claim("verificationStatus", user.get().getOrganizations().getFirst().getOrganization().getVerificationStatus());
//                session.put("verificationStatus", user.get().getOrganizations().getFirst().getOrganization().getVerificationStatus());
            }

            if (isEmployee) {
                Firm firm = user.get().getRole().getFirm();
                FirmForTokenResponseDTO firmForTokenResponseDTO = FirmForTokenResponseDTO.builder()
                        .uuid(firm.getUuid())
                        .logoUrl(firm.getLogoUrl())
                        .name(firm.getName())
                        .email(firm.getEmail())
                        .contactNo(firm.getContactNo())
                        .website(firm.getWebsite())
                        .build();
                builder.claim("organization", firmForTokenResponseDTO);
                session.put("organization", firmForTokenResponseDTO);
            }

            session.put("uuid", String.valueOf(user.get().getUuid()));
//            session.put("avatarUrl", String.valueOf(user.get().getAvatarUrl()));
            session.put("name", user.get().getUsername());
            session.put("roles", userRoles);

            JwtClaimsSet claims = builder.build();

            Map<String, Object> response = new HashMap<>();
            response.put("session", session);
            response.put("token", this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue());

            return response;
        } else {
            return null;
        }
    }

    public Map<String, Object> generateRefreshToken(String email) throws NotFoundException {
        Optional<User> user = userRepository.findByEmailIgnoreCase(email);

        if (user.isPresent()) {
            Instant now = Instant.now();

            JwtClaimsSet.Builder builder = JwtClaimsSet.builder()
                    .issuedAt(now)
                    .expiresAt(now.plusSeconds(refreshTokenExpiration)) // Refresh token validity is typically longer
                    .subject(String.valueOf(user.get().getUuid()))
                    .claim("uuid", String.valueOf(user.get().getUuid()));

            JwtClaimsSet claims = builder.build();

            Map<String, Object> response = new HashMap<>();
            response.put("refreshToken", this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue());

            return response;
        } else {
            throw new NotFoundException("User not found!");
        }
    }

//    public void addCookie(String token, String refreshToken, HttpServletResponse response) {
//        Cookie cookie = new Cookie("AUTH_TOKEN", token);
//        cookie.setHttpOnly(true);
//        cookie.setAttribute("SameSite", "Lax");
//        cookie.setMaxAge(jwtExpiration.intValue());
//        cookie.setPath("/");
//        setCookieDomainAndSecure(cookie);
//        response.addCookie(cookie);
//
//        Cookie refreshCookie = new Cookie("REFRESH_TOKEN", refreshToken);
//        refreshCookie.setHttpOnly(true);
//        refreshCookie.setAttribute("SameSite", "Lax");
//        refreshCookie.setMaxAge(refreshTokenExpiration.intValue());
//        refreshCookie.setPath("/");
//        setCookieDomainAndSecure(refreshCookie);
//        response.addCookie(refreshCookie);
//    }

    public void addCookie(String token, String refreshToken, HttpServletResponse response) {
        Cookie authCookie = new Cookie("AUTH_TOKEN", token);
        authCookie.setHttpOnly(true);
        authCookie.setSecure(true); // must be secure if SameSite=None
        authCookie.setMaxAge(jwtExpiration.intValue());
        authCookie.setPath("/");
        authCookie.setAttribute("SameSite", "None"); // 👈 updated here
        response.addCookie(authCookie);

        Cookie refreshCookie = new Cookie("REFRESH_TOKEN", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setMaxAge(refreshTokenExpiration.intValue());
        refreshCookie.setPath("/");
        refreshCookie.setAttribute("SameSite", "None"); // 👈 updated here
        response.addCookie(refreshCookie);
    }


    public void removeCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("AUTH_TOKEN", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setAttribute("SameSite", "Lax");
        setCookieDomainAndSecure(cookie);
        response.addCookie(cookie);

        Cookie refreshCookie = new Cookie("REFRESH_TOKEN", null);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0);
        refreshCookie.setAttribute("SameSite", "Lax");
        setCookieDomainAndSecure(refreshCookie);
        response.addCookie(refreshCookie);
    }

    private void setCookieDomainAndSecure(Cookie cookie) {
        if (activeEnvironment.equals("prod")) {
            cookie.setSecure(true);
            cookie.setDomain("iotedge.com");
        } else if (activeEnvironment.equals("dev")) {
            cookie.setSecure(true);
            cookie.setDomain("localhost");
        } else if (activeEnvironment.equals("uat")) {
            cookie.setSecure(true);
            cookie.setDomain("iotedge.com");
        } else {
            cookie.setSecure(false);
            cookie.setDomain("localhost");
        }
    }

    public String getSubjectFromRefreshToken(String refreshToken) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(publicKey) // Use public key to validate the signature
                .build()
                .parseClaimsJws(refreshToken)
                .getBody();

        // Return the subject ("sub" claim) from the token
        return claims.getSubject();
    }

    public boolean hasAccessTokenExpired(String token) {
        try {
            // Parse the token and extract claims
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Date expiration = claims.getExpiration();
            Instant now = Instant.now();

            // First check if the token is already expired
            if (expiration.toInstant().isBefore(now)) {
                log.info("Token is expired.. Login again ...");
                // Token is expired
                return true;
            }
            return false;

        } catch (ExpiredJwtException e) {
            // Token is expired
            return false;
        } catch (Exception e) {
            // Handle other parsing exceptions (e.g., malformed token, invalid signature)
            return false;
        }
    }

    public void renewToken(HttpServletRequest request, HttpServletResponse response) throws NotFoundException, IOException, ChangeSetPersister.NotFoundException {
        String refreshToken = jwtUtil.getRefreshTokenFromCookies(request);
        if (refreshToken == null) {
            removeCookie(response);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No refresh token found.");
        } else {
            if (isValidRefreshToken(refreshToken)) {
                String userUuid = getSubjectFromRefreshToken(refreshToken);
                User user = userRepository.findByUuid(UUID.fromString(userUuid));
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userUuid, null, List.of());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                Map<String, Object> jwtResponse = generateAuthenticationToken(user.getEmail());
                Map<String, Object> jwtResponseForRefreshToken = generateRefreshToken(user.getEmail());
                String refreshedAccessToken = jwtResponse.get("token").toString();
                String refreshedRefreshToken = jwtResponseForRefreshToken.get("refreshToken").toString();
                addCookie(refreshedAccessToken, refreshedRefreshToken, response);
                log.info("Refresh & Access Tokens Generated >>>>>>>>>");
                Authentication auth = jwtUtil.getAuthentication(jwtResponse.get("token").toString());
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                removeCookie(response);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid refresh token.");
            }
        }
    }

//    private String extractRefreshFromRequest(HttpServletRequest request) throws UnauthorizedException {
//        Cookie[] cookies = request.getCookies();
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                if (cookie.getName().equals("REFRESH_TOKEN")) {
//                    return cookie.getValue();
//                }
//            }
//        } else {
//            throw new UnauthorizedException("Refresh token not found in cookies.");
//        }
//        return null;
//    }
}

