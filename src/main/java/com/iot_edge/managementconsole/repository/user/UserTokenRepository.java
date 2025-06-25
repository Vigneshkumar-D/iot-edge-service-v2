package com.iot_edge.managementconsole.repository.user;

import com.iot_edge.managementconsole.entity.user.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken,Integer> {
    Optional<UserToken> findByToken(String token);

    List<UserToken> findAllByUserId(Integer userId);
}



