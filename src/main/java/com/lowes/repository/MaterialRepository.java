package com.lowes.repository;


import com.lowes.entity.Material;
import com.lowes.entity.enums.PhaseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MaterialRepository extends JpaRepository<Material, UUID> {

    List<Material> findByRenovationType(PhaseType phaseType);

    List<Material> findByDeleted(boolean deleted);

    List<Material> findByRenovationTypeAndDeleted(PhaseType phaseType, boolean deleted);
}
