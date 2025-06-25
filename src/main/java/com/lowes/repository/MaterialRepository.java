package com.lowes.repository;


import com.lowes.entity.Material;
import com.lowes.entity.enums.RenovationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material,Integer> {

    List<Material> findByRenovationType(RenovationType renovationType);

    List<Material> findByDeleted(boolean deleted);

    List<Material> findByRenovationTypeAndDeleted(RenovationType renovationType, boolean deleted);
}
