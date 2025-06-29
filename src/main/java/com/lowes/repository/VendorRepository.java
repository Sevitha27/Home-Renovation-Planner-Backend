package com.lowes.repository;

import com.lowes.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, UUID> {
    // Optional: Add methods later if needed
    boolean existsByIdAndAvailableTrue(UUID id);

}
