package com.lowes.repository;

import com.lowes.entity.Phase;
import com.lowes.entity.Project;
import com.lowes.entity.enums.PhaseStatus;
import com.lowes.entity.enums.PhaseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface  PhaseRepository extends JpaRepository<Phase, UUID> {
    List<Phase> findByProject_Id(UUID projectId);
    List<Phase> findByEndDateBeforeAndPhaseStatusNot(LocalDate date, PhaseStatus status);
    List<Phase> findByStartDate(LocalDate date);

    boolean existsByProjectAndPhaseType(Project project, PhaseType phaseType);
    @Query("SELECT p FROM Phase p WHERE p.vendor.id = :vendorId AND " +
            "((:startDate BETWEEN p.startDate AND p.endDate) OR " +
            "(:endDate BETWEEN p.startDate AND p.endDate) OR " +
            "(p.startDate BETWEEN :startDate AND :endDate))")
    List<Phase> findConflictingPhasesForVendor(UUID vendorId, LocalDate startDate, LocalDate endDate);

}
