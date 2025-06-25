package com.iot_edge.managementconsole.controller.authentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot_edge.common.exceptions.NotFoundException;
import com.iot_edge.common.exceptions.TooManyRequestsException;
import com.iot_edge.managementconsole.constants.OTPVerificationType;
import com.iot_edge.managementconsole.dto.request.GetOTPRequestDTO;
import com.iot_edge.managementconsole.dto.request.VerifyOTPRequestDTO;
import com.iot_edge.managementconsole.entity.user.User;
import com.iot_edge.managementconsole.repository.user.UserRepository;
import com.iot_edge.managementconsole.service.authentication.AuthenticationService;
import com.iot_edge.managementconsole.service.authentication.OTPService;
import com.iot_edge.managementconsole.service.user.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/otp")
@Slf4j
public class OTPController {
    private final OTPService  otpService;
    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;

    @Autowired
    public OTPController(OTPService otpService, UserService userService, AuthenticationService authenticationService, ObjectMapper objectMapper, UserRepository userRepository) {
        this.otpService = otpService;
        this.userService = userService;
        this.authenticationService = authenticationService;
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
    }

    @PostMapping(value = "/verify-otp", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> verifyEmail(HttpServletResponse response, @RequestBody VerifyOTPRequestDTO verifyOTPRequestDTO) {
        try {
            log.info("Verifying OTP sent on email {}", verifyOTPRequestDTO.getEmail());
            boolean isVerified;
            User user = userRepository.findByEmailIgnoreCase(verifyOTPRequestDTO.getEmail()).orElse(null);
            if (Objects.equals(verifyOTPRequestDTO.getType(), OTPVerificationType.FORGOT_PASSWORD.name())) {
                isVerified = otpService.verifyEmailOTP(verifyOTPRequestDTO.getEmail(), verifyOTPRequestDTO.getOtp(), false);
            } else if (Objects.equals(verifyOTPRequestDTO.getType(), OTPVerificationType.CHANGE_PASSWORD.name())) {
                isVerified = otpService.verifyEmailOTP(verifyOTPRequestDTO.getEmail(), verifyOTPRequestDTO.getOtp(), false);
            }else if (Objects.equals(verifyOTPRequestDTO.getType(), OTPVerificationType.VERIFICATION.name())) {
                isVerified = otpService.verifyEmailOTP(verifyOTPRequestDTO.getEmail(), verifyOTPRequestDTO.getOtp(),true);
                if(isVerified){
                    assert user != null;
                    if(!user.getIsEmailVerified()) {
                        user.setIsEmailVerified(true);
                        userRepository.save(user);
                    }
                    Map<String, Object> jwtResponse = authenticationService.generateAuthenticationToken(user.getEmail());
                    Map<String, Object> jwtResponseForRefreshToken = authenticationService.generateRefreshToken(user.getEmail());
                    authenticationService.addCookie(jwtResponse.get("token").toString(), jwtResponseForRefreshToken.get("refreshToken").toString(), response);
                    return ResponseEntity.ok(objectMapper.writeValueAsString(jwtResponse.get("session")));
                }
            }
            else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request!");
            }
            if (isVerified) {
                return ResponseEntity.ok("OTP verified successfully!");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid OTP!");
            }
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (JsonProcessingException | ChangeSetPersister.NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/get-otp")
    public ResponseEntity<String> requestOTP(@RequestBody GetOTPRequestDTO getOTPRequestDTO) throws IOException, TooManyRequestsException, NotFoundException {
        if (getOTPRequestDTO.getEmail() != null) {
            if (userService.checkIfUserExistAndValidateForOTP(getOTPRequestDTO.getEmail())) {
                log.info("Requesting OTP on email {}", getOTPRequestDTO.getEmail());
                Map<String, Object> emailOTPTemplateData = new HashMap<>();
                otpService.generateOTPForEmail(getOTPRequestDTO.getEmail(), emailOTPTemplateData, null, null, 0);
                return ResponseEntity.ok("OTP successfully sent on email!");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid email!");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request!");
        }
    }
}

