package com.iot_edge.managementconsole.controller.user;

import com.iot_edge.managementconsole.dto.user.UserDTO;
import com.iot_edge.managementconsole.model.user.ResponseModel;
import com.iot_edge.managementconsole.repository.user.RoleRepository;
import com.iot_edge.managementconsole.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @PostMapping
    public ResponseEntity<ResponseModel<?>> createUser(@RequestBody UserDTO userDTO) {
//        Optional<Role> role = roleRepository.findById(userDTO.getRole().getId());
//        userDTO.setRole(role.get());

        return userService.createUser(userDTO);
//        return userService.createUser(userDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<?>> getUserById(@PathVariable("id") Integer userId) {
        return userService.getUserById(userId);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<ResponseModel<?>> updateUser(@PathVariable(name = "id") Integer userId, @RequestBody UserDTO userDTO) {
        return userService.updateUser(userId, userDTO);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<?>> deleteUser(@PathVariable(name = "id") Integer userId) {
        return userService.deleteUser(userId);
    }

    @GetMapping
    public ResponseEntity<ResponseModel<?>> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("current-user")
    public ResponseEntity<ResponseModel<?>> getCurrentUser(){
        return userService.getCurrentUser();
    }
}
