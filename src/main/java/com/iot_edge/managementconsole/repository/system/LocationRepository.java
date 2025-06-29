package com.iot_edge.managementconsole.repository.system;

import com.iot_edge.managementconsole.entity.system.Firm;
import com.iot_edge.managementconsole.entity.system.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LocationRepository extends JpaRepository<Location,Integer>, JpaSpecificationExecutor<Location> {

    Optional<Location> findByUuid(UUID uuid);
}
