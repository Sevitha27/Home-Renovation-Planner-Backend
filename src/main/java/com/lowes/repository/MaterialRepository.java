package com.lowes.repository;


import com.lowes.entity.Material;
import com.lowes.entity.enums.PhaseType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MaterialRepository extends JpaRepository<Material, UUID> {

    Optional<Material> findByExposedId(UUID id);

    List<Material> findByPhaseType(PhaseType phaseType, Sort sort);

    List<Material> findByDeleted(boolean deleted, Sort sort);

    List<Material> findByPhaseTypeAndDeleted(PhaseType phaseType, boolean deleted, Sort sort);

    Page<Material> findByPhaseTypeAndDeleted(PhaseType phaseType, Boolean deleted, Pageable pageable);
    Page<Material> findByPhaseType(PhaseType phaseType, Pageable pageable);
    Page<Material> findByDeleted(Boolean deleted, Pageable pageable);
}
