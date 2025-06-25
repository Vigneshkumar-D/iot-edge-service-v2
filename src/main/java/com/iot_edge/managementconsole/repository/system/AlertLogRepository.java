package com.iot_edge.managementconsole.repository.system;

import com.iot_edge.managementconsole.entity.system.AlertLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertLogRepository extends JpaRepository<AlertLog,Integer>, JpaSpecificationExecutor<AlertLog> {

}
