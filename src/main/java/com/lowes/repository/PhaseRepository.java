package com.lowes.repository;

import com.lowes.entity.Phase;
import com.lowes.entity.enums.PhaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface  PhaseRepository extends JpaRepository<Phase, UUID> {
    List<Phase> findByProject_Id(UUID projectId);
    List<Phase> findByEndDateBeforeAndPhaseStatusNot(LocalDate date, PhaseStatus status);
    List<Phase> findByStartDate(LocalDate date);
}
