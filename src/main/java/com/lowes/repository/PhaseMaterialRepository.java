package com.lowes.repository;


import com.lowes.entity.PhaseMaterial;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PhaseMaterialRepository extends JpaRepository<PhaseMaterial, Integer> {

    Optional<PhaseMaterial> findByExposedId(UUID id);

    void deleteByExposedId(UUID id);
}
