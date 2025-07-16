package com.lowes.repository;

import com.lowes.entity.Phase;
import com.lowes.entity.Project;
import com.lowes.entity.Room;
import com.lowes.entity.Vendor;
import com.lowes.entity.enums.PhaseStatus;
import com.lowes.entity.enums.PhaseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface  PhaseRepository extends JpaRepository<Phase, UUID> {


    List<Phase> findByEndDateBeforeAndPhaseStatusNot(LocalDate date, PhaseStatus status);
    List<Phase> findByStartDate(LocalDate date);


    boolean existsByRoomAndPhaseType(Room room, PhaseType phaseType);

    List<Phase> findAllByRoom_Id(UUID roomId);

    List<Phase> findByVendor(Vendor vendor);
}
