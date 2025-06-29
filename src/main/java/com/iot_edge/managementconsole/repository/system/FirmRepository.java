package com.iot_edge.managementconsole.repository.system;

import com.iot_edge.managementconsole.entity.system.Firm;
import com.iot_edge.managementconsole.entity.user.User;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FirmRepository extends JpaRepository<Firm,Integer>, JpaSpecificationExecutor<Firm> {

    Firm findByName(String name);

    Optional<Firm> findByUuid(UUID firmUuid);

//    Optional<Firm> findByUser(User user);
}
