package com.lowes.repository;


import com.lowes.entity.Skill;
import com.lowes.entity.enums.SkillType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SkillRepository extends JpaRepository<Skill, UUID> {

    Optional<Skill> findByNameAndBasePrice(SkillType name, Double base_price);

   Optional<Skill> findByName(SkillType name);
}
