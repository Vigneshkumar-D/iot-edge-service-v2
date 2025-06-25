package com.iot_edge.managementconsole.repository.authentication;

import com.iot_edge.managementconsole.entity.password.PasswordManagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordManagementRepository extends JpaRepository<PasswordManagement, Long> {
    Optional<PasswordManagement> findByToken(String token);
}
