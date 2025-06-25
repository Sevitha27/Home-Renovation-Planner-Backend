package com.lowes.repository;

import com.lowes.entity.Phase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface  PhaseRepository extends JpaRepository<Phase,Long> {
    List<Phase> findByProject_Id(Long projectId);
}
