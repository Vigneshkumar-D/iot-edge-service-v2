package com.iot_edge.managementconsole.controller.authentication;
//
//import com.iot_edge.managementconsole.model.user.ResponseModel;
//import com.iot_edge.managementconsole.model.user.TokenModel;
//import com.iot_edge.managementconsole.service.authentication.AuthenticationService;
//
//import com.iot_edge.managementconsole.utils.user.JwtUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//
//@RestController
//@RequestMapping("auth")
//public class AuthenticationController {
//
//    @Autowired
//    private AuthenticationService authenticationService;
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    @PostMapping("/login")
//    public ResponseEntity<ResponseModel<?>> login(@RequestBody TokenModel user) {
//            return  authenticationService.verify(user);
//    }
//
//    @PostMapping("/logout")
//    public ResponseEntity<ResponseModel<?>> logout(@RequestHeader("Authorization") String authorization) {
//        return authenticationService.logout(authorization);
//    }
//
//    @PostMapping("/forget-password")
//    public ResponseEntity<ResponseModel<?>> passwordReset(@RequestBody Map<String, String>  email) {
//        return authenticationService.sendResetPasswordLink(email.get("email"));
//    }
//
//    @PostMapping("/confirm-reset")
//    public ResponseEntity<ResponseModel<?>> confirmPasswordReset(@RequestHeader("token") String token, @RequestBody Map<String, String> newPassword) {
//        return authenticationService.resetPassword(token, newPassword.get("newPassword"));
//    }
//}



import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot_edge.common.exceptions.*;
import com.iot_edge.managementconsole.dto.request.AuthenticationRequestDTO;
import com.iot_edge.managementconsole.dto.request.ChangePasswordRequestDTO;
import com.iot_edge.managementconsole.dto.request.ResetPasswordRequestDTO;
import com.iot_edge.managementconsole.dto.user.PingSuccessResponseDTO;
import com.iot_edge.managementconsole.service.authentication.AuthenticationService;
import com.iot_edge.managementconsole.utils.annotations.AuthenticatedUserDetails;
import com.iot_edge.managementconsole.utils.user.AuthenticationDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final AuthenticationService authenticationService;
    private final ObjectMapper objectMapper;

    public AuthController(AuthenticationService authenticationService, ObjectMapper objectMapper) {
        this.authenticationService = authenticationService;
        this.objectMapper = objectMapper;
    }

    @GetMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAuthenticate() throws JsonProcessingException {
        PingSuccessResponseDTO pingSuccessResponseDTO = PingSuccessResponseDTO.builder()
                .status(200)
                .message("UP")
                .build();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(objectMapper.writeValueAsString(pingSuccessResponseDTO));
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> authenticate(HttpServletResponse response, @RequestBody AuthenticationRequestDTO authenticationRequestDTO,
                                               @RequestParam(name = "secret", required = false, defaultValue = "") String secret) {
        log.info("Logging in user : {}", authenticationRequestDTO);
        try {
            return ResponseEntity.ok(objectMapper.writeValueAsString(authenticationService.verifyCredentials(authenticationRequestDTO, response, secret)));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (TooManyRequestsException e) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        log.info("logging out");
        try {
            authenticationService.removeCookie(response);
            return ResponseEntity.ok().body(objectMapper.writeValueAsString("Logged Out Successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/renew-token")
    public ResponseEntity<String> renewToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            log.info("Renewing token");
            authenticationService.renewToken(request, response);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


}
