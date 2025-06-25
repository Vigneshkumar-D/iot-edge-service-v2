package com.iot_edge.managementconsole.repository.system;

import com.iot_edge.managementconsole.entity.system.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetRepository extends JpaRepository<Asset,Integer>, JpaSpecificationExecutor<Asset> {
    Asset findByAssetName(String assetName);
    Asset findBySubTopicName(String topic);
}
