package com.iot_edge.managementconsole.repository.authentication;


import com.iot_edge.managementconsole.entity.authentication.OTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface OTPRepository extends JpaRepository<OTP, Long>, JpaSpecificationExecutor<OTP> {
    Optional<OTP> findByEmail(String email);
    List<OTP> findAllByOtpExpiresAtBefore(Date date);
}
