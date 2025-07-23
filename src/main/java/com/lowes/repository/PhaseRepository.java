package com.lowes.repository;

import com.lowes.entity.Phase;
import com.lowes.entity.Project;
import com.lowes.entity.Room;
import com.lowes.entity.Vendor;
import com.lowes.entity.enums.PhaseStatus;
import com.lowes.entity.enums.PhaseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface  PhaseRepository extends JpaRepository<Phase, UUID> {


    List<Phase> findByEndDateBeforeAndPhaseStatusNot(LocalDate date, PhaseStatus status);
    List<Phase> findByStartDate(LocalDate date);

    @Query("""
    SELECT p FROM Phase p
    JOIN FETCH p.room r
    JOIN FETCH r.project proj
    JOIN FETCH proj.owner o
    WHERE p.startDate = :startDate
""")
    List<Phase> findByStartDateWithDetails(@Param("startDate") LocalDate startDate);
    @Query("""
    SELECT p FROM Phase p
    JOIN FETCH p.room r
    JOIN FETCH r.project proj
    JOIN FETCH proj.owner o
    WHERE p.endDate < :date AND p.phaseStatus <> :status
""")
    List<Phase> findByEndDateBeforeAndPhaseStatusNotWithDetails(
            @Param("date") LocalDate date,
            @Param("status") PhaseStatus status
    );

    boolean existsByRoomExposedIdAndPhaseType(UUID roomExposedId, PhaseType phaseType);

    List<Phase> findAllByRoom_Id(UUID roomId);

    List<Phase> findByVendor(Vendor vendor);

    List<Phase> findAllByRoom_ExposedId(UUID exposedId);
}
