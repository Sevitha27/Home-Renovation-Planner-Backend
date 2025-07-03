package com.lowes.repository;

import com.lowes.entity.Skill;
import com.lowes.entity.User;
import com.lowes.entity.Vendor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, UUID> {
    Vendor findByExposedId(UUID exposedId);

    Page<Vendor> findByApproved(Boolean approved, Pageable pageable);
    Page<Vendor> findByApprovedIsNull(Pageable pageable);

    Vendor findByUser(User user);

    @Query("SELECT COUNT(v) FROM Vendor v JOIN v.skills s WHERE s = :skill")
    long countBySkillsContaining(@Param("skill") Skill skill);
    // Optional: Add methods later if needed
    boolean existsByIdAndAvailableTrue(UUID id);

}
