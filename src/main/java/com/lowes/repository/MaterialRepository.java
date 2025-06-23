package com.lowes.repository;

import com.example.Home_Renovation.entity.Material;
import com.example.Home_Renovation.entity.enums.RenovationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material,Integer> {

    List<Material> findByRenovationType(RenovationType renovationType);
}
