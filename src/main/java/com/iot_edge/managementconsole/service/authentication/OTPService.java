package com.iot_edge.managementconsole.service.authentication;

import com.iot_edge.common.exceptions.NotFoundException;
import com.iot_edge.common.exceptions.TooManyRequestsException;
import com.iot_edge.managementconsole.constants.NotificationChannel;
import com.iot_edge.managementconsole.constants.NotificationStatus;
import com.iot_edge.managementconsole.constants.NotificationType;
import com.iot_edge.managementconsole.constants.VerificationStatus;
import com.iot_edge.managementconsole.entity.authentication.Notification;
import com.iot_edge.managementconsole.entity.authentication.OTP;
import com.iot_edge.managementconsole.entity.authentication.OtpAttempt;
import com.iot_edge.managementconsole.entity.authentication.SystemConfiguration;
import com.iot_edge.managementconsole.entity.system.Firm;
import com.iot_edge.managementconsole.entity.user.Role;
import com.iot_edge.managementconsole.entity.user.User;
import com.iot_edge.managementconsole.repository.authentication.NotificationRepository;
import com.iot_edge.managementconsole.repository.authentication.OTPRepository;
import com.iot_edge.managementconsole.repository.authentication.OtpAttemptRepository;
import com.iot_edge.managementconsole.repository.authentication.SystemConfigurationRepository;
import com.iot_edge.managementconsole.repository.system.FirmRepository;
import com.iot_edge.managementconsole.repository.user.RoleRepository;
import com.iot_edge.managementconsole.repository.user.UserRepository;
import com.iot_edge.managementconsole.utils.user.AuthenticationDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class OTPService {
    private final OTPRepository otpRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final NotificationRepository notificationRepository;
    private final OtpAttemptRepository otpAttemptRepository;
    private final SystemConfigurationRepository systemConfigurationRepository;
    private final RoleRepository userRoleRepository;
    private final FirmRepository firmRepository;

    public OTPService(OTPRepository otpRepository, UserRepository userRepository, EmailService emailService, NotificationRepository notificationRepository, OtpAttemptRepository otpAttemptRepository, SystemConfigurationRepository systemConfigurationRepository, RoleRepository userRoleRepository, FirmRepository firmRepository) {
        this.otpRepository = otpRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.notificationRepository = notificationRepository;
        this.otpAttemptRepository = otpAttemptRepository;
        this.systemConfigurationRepository = systemConfigurationRepository;
        this.userRoleRepository = userRoleRepository;
        this.firmRepository = firmRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public void generateOTPForEmail(String email, Map<String, Object> emailOTPTemplateData, String emailTemplateName, String subject, int genOTP) throws NotFoundException, TooManyRequestsException, IOException {
        Optional<SystemConfiguration> otpMaxRetryLimitConfiguration = systemConfigurationRepository.findByKey("OTP_MAX_RETRY_LIMIT");
        int OTP_MAX_RETRY_LIMIT;
        Optional<User> user = userRepository.findByEmailIgnoreCase(email);
        if (user.isEmpty()) {
            throw new NotFoundException("User not found!");
        }
        if (otpMaxRetryLimitConfiguration.isEmpty()) {
            throw new NotFoundException("OTP max retry limit configuration not found!");
        } else {
            OTP_MAX_RETRY_LIMIT = Integer.parseInt(otpMaxRetryLimitConfiguration.get().getValue());
        }
        Optional<OtpAttempt> otpAttempt = otpAttemptRepository.findByEmail(user.get().getEmail());
        if (otpAttempt.isEmpty()) {
            OtpAttempt newOtpAttempt = OtpAttempt.builder()
                    .email(user.get().getEmail())
                    .noOfAttempts(1)
                    .toBeDeletedAt(LocalDateTime.now().plusMinutes(5))
                    .build();
            otpAttemptRepository.save(newOtpAttempt);
        } else {
            if (otpAttempt.get().getNoOfAttempts() >= OTP_MAX_RETRY_LIMIT) {
                throw new TooManyRequestsException("Exceeded the maximum number of OTP attempts!");
            }
            otpAttempt.get().setNoOfAttempts(otpAttempt.get().getNoOfAttempts() + 1);
            otpAttempt.get().setToBeDeletedAt(LocalDateTime.now().plusMinutes(5));
            otpAttemptRepository.save(otpAttempt.get());
        }

        int generatedOTP;

        if (genOTP != 0) {
            generatedOTP = genOTP;
        } else {
            generatedOTP = generateOTP();
        }

        log.info("Generated OTP for email {} is {}", user.get().getEmail(), generatedOTP);
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

        // Convert Timestamp to Calendar
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(currentTimestamp.getTime());
        // Add 5 minutes
        cal.add(Calendar.MINUTE, 5);
        // Convert Calendar back to Timestamp
        Timestamp expirationTimestamp = new Timestamp(cal.getTimeInMillis());

        String hashedOTP = hashOTP(String.valueOf(generatedOTP));

        Optional<OTP> otpOptional = otpRepository.findByEmail(user.get().getEmail());

        OTP otp;
        if (otpOptional.isPresent()) {
            otp = otpOptional.get();
            otp.setOtpEmail(hashedOTP);
            otp.setOtpEmailCreatedAt(currentTimestamp);
            otp.setOtpExpiresAt(expirationTimestamp);

        } else {
            otp = OTP.builder()
                    .email(user.get().getEmail())
                    .otpEmailCreatedAt(currentTimestamp)
                    .otpEmail(hashedOTP).otpExpiresAt(expirationTimestamp).build();
        }
        otpRepository.save(otp);

        emailOTPTemplateData.put("otp", generatedOTP);
        if (emailTemplateName == null) {
            emailTemplateName = "emailOTPConfirmationTemplate.mustache";
        }
        if (subject == null) {
            subject = "LMS : OTP Confirmation";
        }
        String content = emailService.compileTemplate(emailTemplateName, emailOTPTemplateData);

        // SENDING OTP VIA EMAIL THROUGH SENDGRID
        Notification emailNotification = Notification.builder()
                .channel(NotificationChannel.EMAIL_SENDGRID)
                .type(NotificationType.OTP)
                .scheduledOn(LocalDateTime.now())
                .status(NotificationStatus.PENDING)
                .recipient(user.get().getEmail())
                .content(content)
                .subject(subject)
                .build();
        notificationRepository.save(emailNotification);
    }

    @Transactional(rollbackFor = Exception.class)
    public void generateOTPForUnknownEmail(String email, Map<String, Object> emailOTPTemplateData, String emailTemplateName, String subject, int genOTP) throws NotFoundException, TooManyRequestsException, IOException {
        Optional<SystemConfiguration> otpMaxRetryLimitConfiguration = systemConfigurationRepository.findByKey("OTP_MAX_RETRY_LIMIT");
        int OTP_MAX_RETRY_LIMIT;

        if (otpMaxRetryLimitConfiguration.isEmpty()) {
            throw new NotFoundException("OTP max retry limit configuration not found!");
        } else {
            OTP_MAX_RETRY_LIMIT = Integer.parseInt(otpMaxRetryLimitConfiguration.get().getValue());
        }
        Optional<OtpAttempt> otpAttempt = otpAttemptRepository.findByEmail(email);
        if (otpAttempt.isEmpty()) {
            OtpAttempt newOtpAttempt = OtpAttempt.builder()
                    .email(email)
                    .noOfAttempts(1)
                    .toBeDeletedAt(LocalDateTime.now().plusMinutes(5))
                    .build();
            otpAttemptRepository.save(newOtpAttempt);
        } else {
            if (otpAttempt.get().getNoOfAttempts() >= OTP_MAX_RETRY_LIMIT) {
                throw new TooManyRequestsException("Exceeded the maximum number of OTP attempts!");
            }
            otpAttempt.get().setNoOfAttempts(otpAttempt.get().getNoOfAttempts() + 1);
            otpAttempt.get().setToBeDeletedAt(LocalDateTime.now().plusMinutes(5));
            otpAttemptRepository.save(otpAttempt.get());
        }
        int generatedOTP;

        if (genOTP != 0) {
            generatedOTP = genOTP;
        } else {
            generatedOTP = generateOTP();
        }

        log.info("Generated OTP for unknown email {} is {}", email, generatedOTP);
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

        // Convert Timestamp to Calendar
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(currentTimestamp.getTime());
        // Add 5 minutes
        cal.add(Calendar.MINUTE, 5);
        // Convert Calendar back to Timestamp
        Timestamp expirationTimestamp = new Timestamp(cal.getTimeInMillis());

        String hashedOTP = hashOTP(String.valueOf(generatedOTP));

        Optional<OTP> otpOptional = otpRepository.findByEmail(email);

        OTP otp;
        if (otpOptional.isPresent()) {
            otp = otpOptional.get();
            otp.setOtpEmail(hashedOTP);
            otp.setOtpEmailCreatedAt(currentTimestamp);
            otp.setOtpExpiresAt(expirationTimestamp);

        } else {
            otp = OTP.builder()
                    .email(email)
                    .otpEmailCreatedAt(currentTimestamp)
                    .otpEmail(hashedOTP).otpExpiresAt(expirationTimestamp).build();
        }
        otpRepository.save(otp);

        emailOTPTemplateData.put("otp", generatedOTP);
        if (emailTemplateName == null) {
            emailTemplateName = "emailOTPConfirmationTemplate.mustache";
        }
        if (subject == null) {
            subject = "LMS : OTP Confirmation";
        }
        String content = emailService.compileTemplate(emailTemplateName, emailOTPTemplateData);

        // SENDING OTP VIA EMAIL THROUGH SENDGRID
        Notification emailNotification = Notification.builder()
                .channel(NotificationChannel.EMAIL_SENDGRID)
                .type(NotificationType.OTP)
                .scheduledOn(LocalDateTime.now())
                .status(NotificationStatus.PENDING)
                .recipient(email)
                .content(content)
                .subject(subject)
                .build();
        notificationRepository.save(emailNotification);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean verifyEmailOTP(String email, String otp, boolean clearOTP) throws NotFoundException {
        log.info("Verifying OTP for email {}", email);
        Optional<User> user = userRepository.findByEmailIgnoreCase(email);
        if (user.isEmpty()) {
            throw new NotFoundException("User not found!");
        }
        Optional<OTP> otpOptional = otpRepository.findByEmail(user.get().getEmail());
        if (otpOptional.isPresent()) {
            OTP otpEntity = otpOptional.get();
            if (otpEntity.getOtpExpiresAt() != null && otpEntity.getOtpExpiresAt().before(new Date())) {
                log.info("OTP for email {} has expired", user.get().getEmail());
                return false;
            }
            if (verifyOTPWithHash(otp, otpEntity.getOtpEmail())) {
                log.info("OTP for email {} is verified", user.get().getEmail());
                if (clearOTP) {
                    if (user.get().getIsEmailVerified()) {
                        log.info("Email {} is already verified", user.get().getEmail());
                        otpRepository.deleteById(otpEntity.getId());
                        return true;
                    }

                    user.get().setIsEmailVerified(true);
                    user.get().setIsActive(true);

                    Role userRoles = userRoleRepository.findById(user.get().getRole().getId())
                            .orElseThrow(() -> new NotFoundException("User role not found!"));

                    if (userRoles.getRoleName().equalsIgnoreCase("ROLE_FIRM_ADMIN")) {
                        log.info("User is Organization Admin. Approving User ..");
//                        Firm firm = firmRepository.findByUser(user.get())
//                                .orElseThrow(() -> new NotFoundException("User Organization not found!"));
                    }
                    userRepository.save(user.get());
                    otpRepository.deleteById(otpEntity.getId());
                } else {
                    return true;
                }
                return true;
            } else {
                log.info("OTP for email {} is not verified", user.get().getEmail());
                return false;
            }
        } else {
            log.info("OTP for email {} is not found", email);
            return false;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean verifyUnknownEmailOTP(String email, String otp, boolean clearOTP, AuthenticationDetails authenticationDetails) throws NotFoundException {
        log.info("Verifying OTP for Unknown email {}", email);
        Optional<OTP> otpOptional = otpRepository.findByEmail(email);
        if (otpOptional.isPresent()) {
            OTP otpEntity = otpOptional.get();
            if (otpEntity.getOtpExpiresAt() != null && otpEntity.getOtpExpiresAt().before(new Date())) {
                log.info("OTP for unknown email {} has expired", email);
                return false;
            }
            if (verifyOTPWithHash(otp, otpEntity.getOtpEmail())) {
                log.info("OTP for unknown email {} is verified", email);
                Optional<User> optionalUser = userRepository.findByEmailIgnoreCase(authenticationDetails.getEmail());
                if (optionalUser.isEmpty()) {
                    throw new NotFoundException("User not found!");
                }
                if (clearOTP) {
                    otpRepository.deleteById(otpEntity.getId());
                }
                return true;
            } else {
                log.info("OTP for unknown email {} is not verified", email);
                return false;
            }
        } else {
            log.info("OTP for unknown email {} is not found", email);
            return false;
        }
    }

    public static int generateOTP() {
        // Generate a 6-digit OTP
        return (int) (Math.random() * (999999 - 100000 + 1) + 100000);
    }

    public static String hashOTP(String otp) {
        try {
            // Create a MessageDigest instance for SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Get the hash bytes by digesting the OTP string
            byte[] hashBytes = digest.digest(otp.getBytes(StandardCharsets.UTF_8));

            // Convert the hash bytes to a hexadecimal string representation
            StringBuilder hexString = new StringBuilder();
            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // Handle NoSuchAlgorithmException
            return null;
        }
    }

    public static boolean verifyOTPWithHash(String inputOTP, String storedHashedOTP) {
        try {
            // Create a MessageDigest instance for SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Get the hash bytes by digesting the input OTP string
            byte[] inputHashBytes = digest.digest(inputOTP.getBytes(StandardCharsets.UTF_8));

            // Convert the hash bytes to a hexadecimal string representation
            StringBuilder inputHexString = new StringBuilder();
            for (byte hashByte : inputHashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) inputHexString.append('0');
                inputHexString.append(hex);
            }

            // Compare the hashed OTP from the user input with the stored hashed OTP
            return inputHexString.toString().equals(storedHashedOTP);
        } catch (NoSuchAlgorithmException e) {
            // Handle NoSuchAlgorithmException
            return false;
        }
    }
}
