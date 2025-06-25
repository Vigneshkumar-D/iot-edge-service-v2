package com.iot_edge.managementconsole.repository.system;

import com.iot_edge.managementconsole.entity.system.IoTGateway;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface IoTGatewayRepository extends JpaRepository<IoTGateway,Integer>, JpaSpecificationExecutor<IoTGateway> {

}
