package com.iot_edge.shift.repository;

import com.iot_edge.shift.entity.ShiftAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShiftAllocationRepository extends JpaRepository<ShiftAllocation, Long> {
}
