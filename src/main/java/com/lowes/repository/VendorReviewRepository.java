package com.lowes.repository;

import com.lowes.entity.VendorReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VendorReviewRepository extends JpaRepository<VendorReview, UUID> {
    // Optional: Add custom queries later if needed
}
