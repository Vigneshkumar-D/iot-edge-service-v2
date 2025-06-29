package com.iot_edge.managementconsole.service.user;

import com.iot_edge.common.exceptions.BadRequestException;
import com.iot_edge.common.exceptions.ExpectationFailedException;
import com.iot_edge.common.exceptions.NotFoundException;
import com.iot_edge.managementconsole.dto.request.ChangePasswordRequestDTO;
import com.iot_edge.managementconsole.dto.request.ResetPasswordRequestDTO;
import com.iot_edge.managementconsole.dto.request.UserRequestDTO;
import com.iot_edge.managementconsole.dto.user.RoleDTO;
import com.iot_edge.managementconsole.dto.user.UserDTO;
import com.iot_edge.managementconsole.entity.user.Role;
import com.iot_edge.managementconsole.entity.user.User;
import com.iot_edge.managementconsole.mapper.RoleMapper;
import com.iot_edge.managementconsole.mapper.UserMapper;
import com.iot_edge.managementconsole.model.user.ResponseModel;
import com.iot_edge.managementconsole.repository.user.RoleRepository;
import com.iot_edge.managementconsole.repository.user.UserRepository;
import com.iot_edge.managementconsole.service.authentication.CryptoService;
import com.iot_edge.managementconsole.service.authentication.EmailService;
import com.iot_edge.managementconsole.service.authentication.OTPService;
import com.iot_edge.managementconsole.utils.ExceptionHandler.ExceptionHandlerUtil;
import com.iot_edge.managementconsole.utils.annotations.AuthenticatedUserDetails;
import com.iot_edge.managementconsole.utils.user.AuthenticationDetails;
import com.iot_edge.managementconsole.utils.user.JwtUtil;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class UserService implements UserDetailsService{

    @Autowired
    private PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RoleService roleService;
    private final EmailService emailService;
    private final ModelMapper modelMapper;
    private final ExceptionHandlerUtil exceptionHandlerUtil;
    private final JwtUtil jwtUtil;
    private final HttpServletRequest request;
    private final OTPService otpService;
    private final CryptoService cryptoService;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, RoleService roleService, EmailService emailService, ModelMapper modelMapper, ExceptionHandlerUtil exceptionHandlerUtil, JwtUtil jwtUtil, HttpServletRequest request, OTPService otpService, CryptoService cryptoService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
//        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
        this.emailService = emailService;
        this.modelMapper = modelMapper;
        this.exceptionHandlerUtil = exceptionHandlerUtil;
        this.jwtUtil = jwtUtil;
        this.request = request;
        this.otpService = otpService;
        this.cryptoService = cryptoService;
    }

    public ResponseEntity<ResponseModel<?>> getAllUsers() {
        try{
            return ResponseEntity.ok(new ResponseModel<>(true, "Success", userRepository.findAll()));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error retrieving user details: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage())));
        }
    }

    public ResponseEntity<ResponseModel<?>> updateUser(String userUuid, UserRequestDTO userRequestDTO) {
        try{
            Optional<User> optionalUser = userRepository.findByUuid(UUID.fromString(userUuid));
            Optional<Role> role = roleRepository.findByUuid(userRequestDTO.getRole().getUuid());
            if(optionalUser.isEmpty()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseModel<>(false, "User details not found!"));
            } if(role.isEmpty()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseModel<>(false, "Role details not found!"));
            }

//            if (userDTO.getProfileImage() != null && !userDTO.getProfileImage().isEmpty()) {
//                user.setProfileImage(userDTO.getProfileImage().getBytes());
//            }
            User user = optionalUser.get();
            user.setUsername(userRequestDTO.getUsername());
            user.setEmail(userRequestDTO.getEmail());
            user.setMobileNumber(userRequestDTO.getMobileNumber());
            user.setAvatarUrl(userRequestDTO.getAvatarUrl());
            user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
            role.ifPresent(user::setRole);
            user = userRepository.save(user);

            return ResponseEntity.ok(new ResponseModel<>(true, "Success", UserMapper.INSTANCE.toUserDTO(user)));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error updating user details: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage())));
        }

    }

    public ResponseEntity<ResponseModel<?>> createUser(UserRequestDTO userRequestDTO) {
        try{
            Optional<Role> role = roleRepository.findByUuid(userRequestDTO.getRole().getUuid());
            if(role.isEmpty()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseModel<>(false, "Role Details not found!"));
            }
            User user = User.builder()
                    .username(userRequestDTO.getUsername())
                    .role(RoleMapper.INSTANCE.toRole(userRequestDTO.getRole()))
                    .mobileNumber(userRequestDTO.getMobileNumber())
                    .email(userRequestDTO.getEmail())
                    .avatarUrl(userRequestDTO.getAvatarUrl())
                    .isContactNoVerified(false)
                    .isEmailVerified(false)
                    .password(passwordEncoder.encode(userRequestDTO.getPassword()))
                    .isActive(userRequestDTO.getActive())
                    .build();
//            user.setActive(true);

//            if (userDTO.getProfileImage() != null && !userDTO.getProfileImage().isEmpty()) {
//                user.setProfileImage(userDTO.getProfileImage().getBytes());
//            }
            User savedUser = userRepository.save(user);
            this.sentWelcomeMail(userRequestDTO);

            return ResponseEntity.ok(new ResponseModel<>(true, "Success", UserMapper.INSTANCE.toUserDTO(savedUser)));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error adding user details: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage())));
        }
    }

    public ResponseEntity<ResponseModel<?>> getUserByUuid(String userUuid) {
        try{
            Optional<User> optionalUser = userRepository.findByUuid(UUID.fromString(userUuid));
            return optionalUser.<ResponseEntity<ResponseModel<?>>>map(user -> ResponseEntity.ok(new ResponseModel<>(true, "Success", UserMapper.INSTANCE.toUserDTO(user)))).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseModel<>(false, "User details not found")));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error retrieving user details: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage())));
        }
    }

    public ResponseEntity<ResponseModel<?>> deleteUser(String userUuid){
        try {
            Optional<User> optionalUser = userRepository.findByUuid(UUID.fromString(userUuid));
            if (optionalUser.isEmpty()) {
                throw new ExpectationFailedException("Role Not Found!");
            } else {
                User user = optionalUser.get();
                userRepository.delete(user);
                return ResponseEntity.ok(new ResponseModel<>(true, "Deleted successfully"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error deleting user: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage())));
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

    public void sentWelcomeMail(UserRequestDTO user) throws IOException {
        String rootUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "") + request.getContextPath();
        String  emailTemplateName = "emailOTPConfirmationTemplate.mustache";
        Map<String, Object> welcomeMailTemplateData = new HashMap<>();
        welcomeMailTemplateData.put("username", user.getUsername());
        welcomeMailTemplateData.put("password", user.getPassword());
        welcomeMailTemplateData.put("rootUrl", rootUrl);
        String body = emailService.compileTemplate(emailTemplateName, welcomeMailTemplateData);
        emailService.sendEmail(user.getEmail(), "Welcome to IoT EDge! Get Started Today", body );
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

    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(ResetPasswordRequestDTO resetPasswordRequestDTO) throws ExpectationFailedException, NotFoundException, BadRequestException {
        String DECRYPTED_PASSWORD = cryptoService.decrypt(resetPasswordRequestDTO.getPassword());
        resetPasswordRequestDTO.setPassword(DECRYPTED_PASSWORD);
        if (resetPasswordRequestDTO.getEmail() != null && !resetPasswordRequestDTO.getEmail().isEmpty()) {
            Optional<User> user = userRepository.findByEmailIgnoreCase(resetPasswordRequestDTO.getEmail());
            if (user.isPresent()) {
                boolean isVerified = otpService.verifyEmailOTP(resetPasswordRequestDTO.getEmail(), resetPasswordRequestDTO.getOtp(), true);
                if (isVerified) {
                    user.get().setPassword(passwordEncoder.encode(resetPasswordRequestDTO.getPassword()));
                    userRepository.save(user.get());
                } else {
                    throw new ExpectationFailedException("Email not verified!");
                }
            } else {
                throw new NotFoundException("User not found!");
            }
        } else {
            throw new BadRequestException("Invalid email!");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void changePassword(ChangePasswordRequestDTO changePasswordRequestDTO, @AuthenticatedUserDetails AuthenticationDetails authenticationDetails) throws BadRequestException, NotFoundException, ExpectationFailedException {
        Optional<User> optionalUser = userRepository.findByUuid(UUID.fromString(authenticationDetails.getUuid()));

        if (optionalUser.isEmpty()) {
            throw new ExpectationFailedException("Role Not Found!");
        }
        User user = optionalUser.get();
        String DECRYPTED_CURRENT_PASSWORD = cryptoService.decrypt(changePasswordRequestDTO.getCurrentPassword());
        String DECRYPTED_NEW_PASSWORD = cryptoService.decrypt(changePasswordRequestDTO.getNewPassword());

        changePasswordRequestDTO.setCurrentPassword(DECRYPTED_CURRENT_PASSWORD);
        changePasswordRequestDTO.setNewPassword(DECRYPTED_NEW_PASSWORD);

        boolean otpResult = otpService.verifyEmailOTP(user.getEmail(), changePasswordRequestDTO.getOtp(), true);

        if (otpResult) {
            if (passwordEncoder.matches(changePasswordRequestDTO.getCurrentPassword(), user.getPassword())) {
                user.setPassword(passwordEncoder.encode(changePasswordRequestDTO.getNewPassword()));
                userRepository.save(user);
            } else {
                throw new BadRequestException("Invalid current password!");
            }
        } else {
            throw new BadRequestException("Invalid OTP!");
        }
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public Page<UserDTO> getAllUsersForLoggedInUser(
            String search,
            Pageable pageable,
            List<String> sort,
            AuthenticationDetails authenticationDetails) throws BadRequestException {

        Specification<User> specification = (root, query, cb) -> {
            String searchPattern = "%" + search.toLowerCase() + "%";
            List<Predicate> predicates = new ArrayList<>();

            // Filter by firm
            predicates.add(cb.equal(root.get("firm").get("uuid"), UUID.fromString(authenticationDetails.getOrganizationUuid())));

            // Search
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("name")), searchPattern),
                    cb.like(cb.lower(root.get("email")), searchPattern),
                    cb.like(cb.lower(root.get("mobileNumber")), searchPattern),
                    cb.like(cb.lower(root.get("role")), searchPattern),
                    cb.like(cb.lower(root.get("active")), searchPattern),
                    cb.like(cb.lower(root.get("createdDate")), searchPattern)
            ));

            assert query != null;
            query.distinct(true);
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return returnAllRolesSorted(pageable, sort, specification);
    }

    private Page<UserDTO> returnAllRolesSorted(Pageable pageable, List<String> sort, Specification<User> specification) throws BadRequestException {
        Page<User> userPage = userRepository.findAll(specification, sanitizeFirmPageable(pageable));

        if (sort.isEmpty()) {
            return userPage.map(UserMapper.INSTANCE::toUserDTO);
        }

        Page<UserDTO> userDTOS = userPage.map(UserMapper.INSTANCE::toUserDTO);

        Comparator<UserDTO> comparator = (a, b) -> 0;

        if (sort.contains("rolename")) {
            comparator = Comparator.comparing(UserDTO::getUsername, String.CASE_INSENSITIVE_ORDER);
        } else if (sort.contains("email")) {
            comparator = Comparator.comparing(UserDTO::getEmail);
        } else if (sort.contains("mobileNumber")) {
            comparator = Comparator.comparing(UserDTO::getMobileNumber);
        }else if (sort.contains("createdDate")) {
            comparator = Comparator.comparing(UserDTO::getCreatedDate);
        }else if (sort.contains("role")) {
            comparator = Comparator.comparing(
                    user -> {
                        RoleDTO role = user.getRole();
                        return role != null ? role.getRoleName() : null;
                    },
                    Comparator.nullsLast(Comparator.naturalOrder())
            );
        } else {
            throw new BadRequestException("Invalid sort parameter!");
        }

        if (sort.contains("DESC")) {
            comparator = comparator.reversed();
        }

        List<UserDTO> sortedUsers = userDTOS.stream().sorted(comparator).toList();
        return new PageImpl<>(sortedUsers, pageable, userDTOS.getTotalElements());
    }

    private Pageable sanitizeFirmPageable(Pageable pageable) {
        Map<String, String> propertyMapping = Map.of(
                "username", "username",
                "email", "email",
                "mobileNumber", "mobileNumber",
                "role", "role",
                "createdDate", "createdDate"
        );

        List<Sort.Order> sanitizedOrders = pageable.getSort().stream()
                .map(order -> {
                    String mappedProperty = propertyMapping.get(order.getProperty());
                    if (mappedProperty != null && !mappedProperty.isEmpty()) {
                        return new Sort.Order(order.getDirection(), mappedProperty);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toList();

        Sort sanitizedSort = sanitizedOrders.isEmpty() ? Sort.unsorted() : Sort.by(sanitizedOrders);
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sanitizedSort);
    }

}
