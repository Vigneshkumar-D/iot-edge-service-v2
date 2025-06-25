package com.iot_edge.shift.repository;

import com.iot_edge.shift.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {
    @Query("SELECT s FROM Shift s WHERE :time BETWEEN s.startTime AND s.endTime")
    Optional<Shift> findShiftByCurrentTime(@Param("time") Instant time);
}
