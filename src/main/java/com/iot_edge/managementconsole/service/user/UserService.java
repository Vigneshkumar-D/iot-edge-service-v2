package com.iot_edge.managementconsole.service.user;

import com.iot_edge.managementconsole.dto.user.UserDTO;
import com.iot_edge.managementconsole.entity.user.Role;
import com.iot_edge.managementconsole.entity.user.User;
import com.iot_edge.managementconsole.model.user.ResponseModel;
import com.iot_edge.managementconsole.repository.user.RoleRepository;
import com.iot_edge.managementconsole.repository.user.UserRepository;
import com.iot_edge.managementconsole.service.authentication.EmailService;
import com.iot_edge.managementconsole.utils.ExceptionHandler.ExceptionHandlerUtil;
import com.iot_edge.managementconsole.utils.user.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

@Service
@Slf4j
public class UserService implements UserDetailsService{

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final RoleService roleService;

    private final EmailService emailService;

    private final ModelMapper modelMapper;

    private final ExceptionHandlerUtil exceptionHandlerUtil;

    private final JwtUtil jwtUtil;

    private final HttpServletRequest request;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, RoleService roleService, EmailService emailService, ModelMapper modelMapper, ExceptionHandlerUtil exceptionHandlerUtil, JwtUtil jwtUtil, HttpServletRequest request) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
//        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
        this.emailService = emailService;
        this.modelMapper = modelMapper;
        this.exceptionHandlerUtil = exceptionHandlerUtil;
        this.jwtUtil = jwtUtil;
        this.request = request;
    }

    public ResponseEntity<ResponseModel<?>> getAllUsers() {
        try{
            return ResponseEntity.ok(new ResponseModel<>(true, "Success", userRepository.findAll()));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error retrieving user details: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage())));
        }
    }

    public ResponseEntity<ResponseModel<?>> updateUser(Integer userId, UserDTO userDTO) {
        try{
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return null;
            }
            Optional<Role> role = roleRepository.findById(userDTO.getRole().getId());
            if(role.isEmpty()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseModel<>(false, "Role details not found!"));
            }

//            if (userDTO.getProfileImage() != null && !userDTO.getProfileImage().isEmpty()) {
//                user.setProfileImage(userDTO.getProfileImage().getBytes());
//            }

            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            modelMapper.getConfiguration().setPropertyCondition(conditions -> {
                return conditions.getSource() != null;
            });
            modelMapper.map(userDTO, user);
            role.ifPresent(user::setRole);

            return ResponseEntity.ok(new ResponseModel<>(true, "Success", userRepository.save(user)));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error updating user details: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage())));
        }

    }

    public ResponseEntity<ResponseModel<?>> createUser(UserDTO userDTO) {
        try{
            Optional<Role> role = roleRepository.findById(userDTO.getRole().getId());
            if(role.isEmpty()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseModel<>(false, "Role Details not found!"));
            }
            User user = new User();
            user.setUsername(userDTO.getUsername());
            user.setRole(role.get());
            user.setEmail(userDTO.getEmail());
            user.setIsActive(userDTO.getActive());
            user.setMobileNumber(userDTO.getMobileNumber());
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            user.setAvatarUrl(userDTO.getAvatarUrl());
//            user.setActive(true);

//            if (userDTO.getProfileImage() != null && !userDTO.getProfileImage().isEmpty()) {
//                user.setProfileImage(userDTO.getProfileImage().getBytes());
//            }
            User savedUser = userRepository.save(user);
            this.sentWelcomeMail(userDTO);

            return ResponseEntity.ok(new ResponseModel<>(true, "Success", savedUser));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error adding user details: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage())));
        }
    }


    public ResponseEntity<ResponseModel<?>> getUserById(Integer userId) {
        try{
            return ResponseEntity.ok(new ResponseModel<>(true, "Success", userRepository.findById(userId)));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error retrieving user details: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage())));
        }

    }

    public ResponseEntity<ResponseModel<?>> deleteUser(Integer userId){
        try {
            if (!userRepository.existsById(userId)) {
                // Return 404 Not Found
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseModel<>(false, "User not found"));
            }
            userRepository.deleteById(userId);
            // Return 200 OK if the category is deleted successfully
            return ResponseEntity.ok(new ResponseModel<>(true, "Deleted successfully"));
        } catch (Exception e) {
            // Return 500 Internal Server Error for any unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error deleting user: " + e.getMessage()));
        }
    }

    public String encodePassword(String password) {
        return this.passwordEncoder.encode(password);
    }

    public ResponseEntity<ResponseModel<?>> getCurrentUser() {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                Object principal = authentication.getPrincipal();
                if (principal instanceof String) {
                    return ResponseEntity.ok(new ResponseModel<>(true, "Success", userRepository.findByUsername((String) principal).get()));
                } else if (principal instanceof UserDetails) {
                    String username = ((UserDetails) principal).getUsername();
                    return ResponseEntity.ok(new ResponseModel<>(true, "Success", userRepository.findByUsername(username).get()));
                }
            }
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error retrieving current user details: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage())));
        }
        return null;
    }

    public void sentWelcomeMail(UserDTO user) throws IOException {
        System.out.println("In user mail");
        String rootUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "") + request.getContextPath();
        String body = "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; background-color: #f9f9f9; color: #333; margin: 0; padding: 0; }" +
                ".container { max-width: 600px; margin: 20px auto; background: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }" +
                "h3 { color: #007bff; }" +
                "p { font-size: 14px; line-height: 1.5; }" +
                "a { color: #007bff; text-decoration: none; }" +
                "a:hover { text-decoration: underline; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<h3>Welcome, " + user.getUsername() + "!</h3>" +
                "<p>We are thrilled to have you as part of SmartRetailEdge! Your account has been successfully created. Here are your login credentials:</p>" +
                "<p><strong>Username:</strong> " + user.getUsername() + "</p>" +
                "<p><strong>Password:</strong> " + user.getPassword() + "</p>" +
                "<p>Click the button below to log in and start exploring:</p>" +
                "<div style='text-align: center; margin: 20px 0;'>" +
                "<a href='"+rootUrl+"/login' style='background: #007bff; color: #fff; padding: 10px 20px; border-radius: 5px; font-size: 16px;'>Log In to Your Account</a>" +
                "</div>" +
                "<p>If you wish to set a new password, you can use the 'Forgot Password' option on the login page to reset it at any time.</p>" +
                "<p>If you have any questions or need assistance, feel free to reach out to our support team.</p>" +
                "<p>We hope you enjoy using SmartRetailEdge!</p>" +
                "<p>Regards,<br>Team SmartRetailEdge</p>" +
                "</div>" +
                "</body>" +
                "</html>";
        emailService.sendEmail(user.getEmail(), "Welcome to SmartRetailEdge! Get Started Today", body );
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), new ArrayList<>());
    }

    public boolean checkIfUserExistAndValidateForOTP(String email) {
        if (email != null) {
            Optional<User> emailUser = userRepository.findByEmailIgnoreCase(email);
            if (emailUser.isPresent()) {
                log.info("checkIfUserExistAndValidateForOTP Email VALIDATED");
                return true;
            } else {
                log.info("checkIfUserExistAndValidateForOTP Email NOT VALIDATED");
                return false;
            }
        } else {
            log.info("checkIfUserExistAndValidateForOTP ERROR");
            return false;
        }
    }

}
