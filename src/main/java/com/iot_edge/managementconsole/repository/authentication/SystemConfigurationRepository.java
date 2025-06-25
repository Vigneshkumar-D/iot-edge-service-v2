package com.iot_edge.managementconsole.repository.authentication;


import com.iot_edge.managementconsole.entity.authentication.SystemConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemConfigurationRepository extends JpaRepository<SystemConfiguration,Long>, JpaSpecificationExecutor<SystemConfiguration> {
    Optional<SystemConfiguration> findByKey(String key);
}

