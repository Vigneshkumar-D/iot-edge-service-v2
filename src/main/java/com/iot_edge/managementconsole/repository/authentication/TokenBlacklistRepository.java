package com.iot_edge.managementconsole.repository.authentication;

import com.iot_edge.managementconsole.entity.authentication.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, Long> {

    Boolean existsByToken(String token);

    void deleteByExpirationDateBefore(LocalDateTime now);
}
