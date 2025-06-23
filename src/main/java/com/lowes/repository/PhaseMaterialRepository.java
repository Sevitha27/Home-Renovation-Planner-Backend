package com.lowes.repository;

import com.example.Home_Renovation.entity.PhaseMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhaseMaterialRepository extends JpaRepository<PhaseMaterial,Integer> {
    List<PhaseMaterial> findByPhaseId(int phaseId);
}
