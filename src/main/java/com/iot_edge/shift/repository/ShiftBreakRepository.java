package com.iot_edge.shift.repository;

import com.iot_edge.shift.entity.ShiftBreaks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShiftBreakRepository extends JpaRepository<ShiftBreaks, Long> {
    List<ShiftBreaks> findByShiftId(Long shiftId);
}
