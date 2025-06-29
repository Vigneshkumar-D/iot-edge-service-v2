package com.iot_edge.managementconsole.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot_edge.common.exceptions.BadRequestException;
import com.iot_edge.common.exceptions.ExpectationFailedException;
import com.iot_edge.common.exceptions.NotFoundException;
import com.iot_edge.managementconsole.dto.request.ChangePasswordRequestDTO;
import com.iot_edge.managementconsole.dto.request.ResetPasswordRequestDTO;
import com.iot_edge.managementconsole.dto.request.UserRequestDTO;
import com.iot_edge.managementconsole.dto.user.UserDTO;
import com.iot_edge.managementconsole.model.user.ResponseModel;
import com.iot_edge.managementconsole.repository.user.RoleRepository;
import com.iot_edge.managementconsole.service.user.UserService;
import com.iot_edge.managementconsole.utils.annotations.AuthenticatedUserDetails;
import com.iot_edge.managementconsole.utils.user.AuthenticationDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;
    private final ObjectMapper objectMapper;

    public UserController(UserService userService, ObjectMapper objectMapper) {
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("get-all-users-paged")
    public ResponseEntity<String> getAllLocationsForLoggedInUser(
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "false") boolean desc,
            @RequestParam(required = false, defaultValue = "createdDate") List<String> sort,
            @AuthenticatedUserDetails AuthenticationDetails authenticationDetails) {
        try {
            Sort _sort;
            Sort.Direction _direction = desc ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable;
            if (sort.contains("createdDate")) {
                _sort = Sort.by(Sort.Direction.DESC, "createdDate");
                sort.add("DESC");
                pageable = PageRequest.of(page, size, _sort);
            } else {
                _sort = Sort.by(_direction, sort.getFirst());
                sort.add(_direction.isAscending() ? "ASC" : "DESC");
                pageable = PageRequest.of(page, size, _sort);
            }
            return ResponseEntity.ok(objectMapper.writeValueAsString(userService.getAllUsersForLoggedInUser(search, pageable, sort, authenticationDetails)));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PostMapping
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseModel<?>> createUser(@RequestBody UserRequestDTO userRequestDTO) {
        try {
            return userService.createUser(userRequestDTO);
        } catch (Exception e) {
            ResponseModel<?> response = new ResponseModel<>(false, "Something went wrong: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{userUuid}")
    public ResponseEntity<ResponseModel<?>> getUserById(@PathVariable("userUuid") String userUuid) {
        try {
            return userService.getUserByUuid(userUuid);
        } catch (Exception e) {
            ResponseModel<?> response = new ResponseModel<>(false, "Something went wrong: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping(value = "/{userUuid}")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseModel<?>> updateUser(@PathVariable(name = "userUuid") String userUuid, @RequestBody UserRequestDTO userRequestDTO) {
        try {
            return userService.updateUser(userUuid, userRequestDTO);
        } catch (Exception e) {
            ResponseModel<?> response = new ResponseModel<>(false, "Something went wrong: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{userUuid}")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseModel<?>> deleteUser(@PathVariable(name = "userUuid") String userUuid) {
        try {
            return userService.deleteUser(userUuid);
        } catch (Exception e) {
            ResponseModel<?> response = new ResponseModel<>(false, "Something went wrong: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseModel<?>> getAllUsers() {
        try {
            return userService.getAllUsers();
        } catch (Exception e) {
            ResponseModel<?> response = new ResponseModel<>(false, "Something went wrong: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("current-user")
    public ResponseEntity<ResponseModel<?>> getCurrentUser(){
        try {
            return userService.getCurrentUser();
        } catch (Exception e) {
            ResponseModel<?> response = new ResponseModel<>(false, "Something went wrong: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping(value = "/reset-password", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequestDTO resetPasswordRequestDTO) {
        log.info("Resetting password {}", resetPasswordRequestDTO);
        try {
            userService.resetPassword(resetPasswordRequestDTO);
            return ResponseEntity.ok(objectMapper.writeValueAsString("Password reset successfully!"));
        } catch (ExpectationFailedException e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping(value = "/change-password", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequestDTO changePasswordRequestDTO, @AuthenticatedUserDetails AuthenticationDetails authenticationDetails) {
        log.info("Changing password");
        try {
            userService.changePassword(changePasswordRequestDTO, authenticationDetails);
            return ResponseEntity.ok(objectMapper.writeValueAsString("Password changed successfully!"));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
