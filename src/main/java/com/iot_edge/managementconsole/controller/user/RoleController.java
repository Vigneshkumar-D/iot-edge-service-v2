package com.iot_edge.managementconsole.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot_edge.common.exceptions.BadRequestException;
import com.iot_edge.managementconsole.dto.request.RoleRequestDTO;
import com.iot_edge.managementconsole.dto.user.RoleDTO;
import com.iot_edge.managementconsole.model.user.ResponseModel;
import com.iot_edge.managementconsole.service.user.RoleService;
import com.iot_edge.managementconsole.utils.annotations.AuthenticatedUserDetails;
import com.iot_edge.managementconsole.utils.user.AuthenticationDetails;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
public class RoleController {

    private final RoleService roleService;

    private final ObjectMapper objectMapper;

    public RoleController(RoleService roleService, ObjectMapper objectMapper) {
        this.roleService = roleService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("get-all-roles-paged")
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
            return ResponseEntity.ok(objectMapper.writeValueAsString(roleService.getAllRolesForLoggedInUser(search, pageable, sort, authenticationDetails)));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PostMapping
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseModel<?>> createRole(@RequestBody RoleRequestDTO roleRequestDTO) {
        try {
            return roleService.createRole(roleRequestDTO);
        } catch (Exception e) {
            ResponseModel<?> response = new ResponseModel<>(false, "Something went wrong: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{roleUuid}")
    public ResponseEntity<ResponseModel<?>> getRoleByUuid(@PathVariable(name = "roleUuid") String roleUuid) {
        try {
            return roleService.getRoleByUuid(roleUuid);
        } catch (Exception e) {
            ResponseModel<?> response = new ResponseModel<>(false, "Something went wrong: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{roleUuid}")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseModel<?>> updateRole(@PathVariable(name = "roleUuid") String roleUuid, @RequestBody RoleDTO roleDTO) {
        try {
            return roleService.updateRole(roleUuid, roleDTO);
        } catch (Exception e) {
            ResponseModel<?> response = new ResponseModel<>(false, "Something went wrong: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{roleUuid}")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseModel<?>> deleteRole(@PathVariable(name = "roleUuid") String roleUuid) {
        try {
            return roleService.deleteRole(roleUuid);
        } catch (Exception e) {
            ResponseModel<?> response = new ResponseModel<>(false, "Something went wrong: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseModel<?>> getAllRoles() {
        try {
            return roleService.getAllRoles();
        } catch (Exception e) {
            ResponseModel<?> response = new ResponseModel<>(false, "Something went wrong: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
