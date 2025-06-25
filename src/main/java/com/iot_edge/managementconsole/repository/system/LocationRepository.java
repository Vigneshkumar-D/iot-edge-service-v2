package com.iot_edge.managementconsole.repository.system;

import com.iot_edge.managementconsole.entity.system.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location,Integer>, JpaSpecificationExecutor<Location> {

}
