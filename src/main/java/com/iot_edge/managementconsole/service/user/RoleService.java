package com.iot_edge.managementconsole.service.user;

import com.iot_edge.managementconsole.dto.user.RoleDTO;
import com.iot_edge.managementconsole.entity.user.Role;
import com.iot_edge.managementconsole.model.user.ResponseModel;
import com.iot_edge.managementconsole.repository.user.RoleRepository;
import com.iot_edge.managementconsole.utils.ExceptionHandler.ExceptionHandlerUtil;
import jakarta.annotation.PostConstruct;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Service
public class RoleService {


    private final RoleRepository roleRepository;

    private final ModelMapper modelMapper;

    private final ExceptionHandlerUtil exceptionHandlerUtil;

    public RoleService(RoleRepository roleRepository, ModelMapper modelMapper, ExceptionHandlerUtil exceptionHandlerUtil) {
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
        this.exceptionHandlerUtil = exceptionHandlerUtil;
    }

    public ResponseEntity<ResponseModel<?>> getAllRoles() {
        try{
            return ResponseEntity.ok(new ResponseModel<>(true, "Success", roleRepository.findAll()));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error retrieving role details: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage()), 500));
        }
    }

    public ResponseEntity<ResponseModel<?>> createRole(RoleDTO roleDto) {
        try{
            Role role = modelMapper.map(roleDto, Role.class);
            return ResponseEntity.ok(new ResponseModel<>(true, "Success", roleRepository.save(role)));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error adding role details: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage())));
        }
    }

    public ResponseEntity<ResponseModel<?>> getRoleById(Integer id) {
        try{

            return ResponseEntity.ok(new ResponseModel<>(true, "Success", roleRepository.findById(id)));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error retrieving role details: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage())));
        }
    }

    public ResponseEntity<ResponseModel<?>> updateRole(Integer roleId, RoleDTO roleDetails) {
        try{
            Role role = roleRepository.findById(roleId).orElse(null);
            if (role == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ResponseModel<>(false, "Role details not found!"));
            }
            modelMapper.map(roleDetails, role);
            return ResponseEntity.ok(new ResponseModel<>(true, "Success", roleRepository.save(role)));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error updating role details: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage())));
        }
    }

    public ResponseEntity<ResponseModel<?>> deleteRole(Integer roleId) {

        try {
            if (!roleRepository.existsById(roleId)) {
                // Return 404 Not Found
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseModel<>(false, "Role not found"));
            }
            roleRepository.deleteById(roleId);
            // Return 200 OK if the category is deleted successfully
            return ResponseEntity.ok(new ResponseModel<>(true, "Deleted successfully"));
        } catch (Exception e) {
            // Return 500 Internal Server Error for any unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error deleting role: " + e.getMessage()));
        }
    }

    public Role getSystem(){
        Optional<Role> optionalRole = this.roleRepository.findByRoleName("System");
        return optionalRole.orElse(null);
    }
    public Role getSuperUser(){
        Optional<Role> optionalRole = this.roleRepository.findByRoleName("Super User");
        return optionalRole.orElse(null);
    }
    public Role getGuest(){
        Optional<Role> optionalRole = this.roleRepository.findByRoleName("Guest");
        return optionalRole.orElse(null);
    }
}
