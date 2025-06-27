package com.lowes.repository;


import com.lowes.entity.PhaseMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PhaseMaterialRepository extends JpaRepository<PhaseMaterial, UUID> {
    List<PhaseMaterial> findByPhaseId(int phaseId);
}
