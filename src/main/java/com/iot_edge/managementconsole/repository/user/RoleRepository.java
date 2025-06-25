package com.iot_edge.managementconsole.repository.user;

import com.iot_edge.managementconsole.entity.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role,Integer>, JpaSpecificationExecutor<Role> {
    Optional<Role> findByRoleName(String roleName);
}
