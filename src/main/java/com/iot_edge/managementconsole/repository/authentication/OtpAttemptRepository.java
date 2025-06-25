package com.iot_edge.managementconsole.repository.authentication;


import com.iot_edge.managementconsole.entity.authentication.OtpAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OtpAttemptRepository extends JpaRepository<OtpAttempt, Long>, JpaSpecificationExecutor<OtpAttempt> {

    Optional<OtpAttempt> findByEmail(String email);

    List<OtpAttempt> findAllByToBeDeletedAtBefore(LocalDateTime toBeDeletedAt);
}

