package com.iot_edge.managementconsole.controller.user;

import com.iot_edge.managementconsole.dto.user.RoleDTO;
import com.iot_edge.managementconsole.model.user.ResponseModel;
import com.iot_edge.managementconsole.service.user.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping
    public ResponseEntity<ResponseModel<?>> createRole(@RequestBody RoleDTO roleDTO) {
        return roleService.createRole(roleDTO);

    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<?>> getRoleById(@PathVariable(name = "id") Integer roleId) {
        return roleService.getRoleById(roleId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<?>> updateRole(@PathVariable(name = "id") Integer roleId, @RequestBody RoleDTO roleDTO) {
        return roleService.updateRole(roleId, roleDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<?>> deleteRole(@PathVariable(name = "id") Integer roleId) {
        return roleService.deleteRole(roleId);
    }

    @GetMapping
    public ResponseEntity<ResponseModel<?>> getAllRoles() {
        return roleService.getAllRoles();
    }
}
