package com.lowes.repository;


import com.lowes.entity.Skill;
import com.lowes.entity.enums.SkillType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SkillRepository extends JpaRepository<Skill, UUID> {
    
    Optional<Skill> findByNameAndBasePrice(SkillType name, Double base_price);

    @Query("SELECT s FROM Skill s WHERE s.vendors IS EMPTY")
    List<Skill> findSkillsWithNoVendors();
    List<Skill> findByName(SkillType name);
   // Optional<Skill> findByName(SkillType name);
}
