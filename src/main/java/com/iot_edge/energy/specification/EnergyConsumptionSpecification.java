package com.iot_edge.energy.specification;

import com.iot_edge.energy.entity.EnergyConsumption;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

public class EnergyConsumptionSpecification {

    public static Specification<EnergyConsumption> filterByAssetId(Integer assetId) {
        return (root, query, criteriaBuilder) -> 
                assetId != null ? criteriaBuilder.equal(root.get("assetId"), assetId) : null;
    }

    public static Specification<EnergyConsumption> filterByDateRange(Instant start, Instant end) {
        return (root, query, criteriaBuilder) -> {
            if (start != null && end != null) {
                return criteriaBuilder.between(root.get("timestamp"), start, end);
            } else if (start != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("timestamp"), start);
            } else if (end != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("timestamp"), end);
            }
            return null;
        };
    }

    public static Specification<EnergyConsumption> filterByPowerFactor(Double minFactor, Double maxFactor) {
        return (root, query, criteriaBuilder) -> {
            if (minFactor != null && maxFactor != null) {
                return criteriaBuilder.between(root.get("powerFactor"), minFactor, maxFactor);
            } else if (minFactor != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("powerFactor"), minFactor);
            } else if (maxFactor != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("powerFactor"), maxFactor);
            }
            return null;
        };
    }
}
