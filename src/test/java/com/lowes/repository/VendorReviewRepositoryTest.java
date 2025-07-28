package com.lowes.repository;

import com.lowes.entity.VendorReview;
import com.lowes.entity.User;
import com.lowes.entity.Vendor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.UUID;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class VendorReviewRepositoryTest {

    @Autowired
    private VendorReviewRepository vendorReviewRepository;

    @Test
    void testSaveAndFindById() {
        // Create dummy VendorReview entity
        VendorReview review = VendorReview.builder()
                .id(UUID.randomUUID()) // or leave null and let JPA generate
                .comment("Great service")
                .rating(4.5)
                .build();

        // Save
        VendorReview savedReview = vendorReviewRepository.save(review);
        assertNotNull(savedReview);
        assertNotNull(savedReview.getId());

        // Find
        Optional<VendorReview> found = vendorReviewRepository.findById(savedReview.getId());
        assertTrue(found.isPresent());
        assertEquals("Great service", found.get().getComment());
        assertEquals(4.5, found.get().getRating());
    }

    @Test
    void testDeleteById() {
        VendorReview review = VendorReview.builder()
                .comment("To be deleted")
                .rating(3.0)
                .build();

        VendorReview savedReview = vendorReviewRepository.save(review);
        UUID id = savedReview.getId();

        vendorReviewRepository.deleteById(id);

        Optional<VendorReview> found = vendorReviewRepository.findById(id);
        assertFalse(found.isPresent());
    }
}
