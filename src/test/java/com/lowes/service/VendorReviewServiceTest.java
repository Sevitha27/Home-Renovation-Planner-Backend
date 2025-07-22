package com.lowes.service;

import com.lowes.dto.request.VendorReviewRequestDTO;
import com.lowes.entity.*;
import com.lowes.entity.enums.SkillType;
import com.lowes.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VendorReviewServiceTest {

    @InjectMocks
    private VendorReviewService vendorReviewService;

    @Mock
    private SkillRepository skillRepository;
    @Mock
    private VendorRepository vendorRepository;
    @Mock
    private VendorReviewRepository vendorReviewRepository;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddReview_success() {
        UUID vendorId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Vendor vendor = Vendor.builder().exposedId(vendorId).reviews(new ArrayList<>()).build();
        User user = User.builder().exposedId(userId).build();

        VendorReviewRequestDTO dto = new VendorReviewRequestDTO();
        dto.setVendorId(vendorId);
        dto.setUserId(userId);
        dto.setRating(4.5);
        dto.setComment("Good service");

        when(vendorRepository.findByExposedId(vendorId)).thenReturn(vendor);
        when(userRepository.findByExposedId(userId)).thenReturn(user);

        vendorReviewService.addReview(dto);

        verify(vendorReviewRepository, times(1)).save(any(VendorReview.class));
    }

    @Test
    void testAddReview_invalidRating() {
        UUID dummyVendorId = UUID.randomUUID();
        UUID dummyUserId = UUID.randomUUID();

        VendorReviewRequestDTO dto = new VendorReviewRequestDTO();
        dto.setRating(6.0);  // Invalid rating
        dto.setVendorId(dummyVendorId);
        dto.setUserId(dummyUserId);

        // Mock vendor and user to avoid triggering "not found" error
        when(vendorRepository.findByExposedId(dummyVendorId))
                .thenReturn(Vendor.builder().exposedId(dummyVendorId).build());
        when(userRepository.findByExposedId(dummyUserId))
                .thenReturn(User.builder().exposedId(dummyUserId).build());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            vendorReviewService.addReview(dto);
        });

        assertEquals("Rating must be between 1 and 5", ex.getMessage());
    }


    @Test
    void testDeleteReview() {
        UUID reviewId = UUID.randomUUID();
        vendorReviewService.deleteReview(reviewId);
        verify(vendorReviewRepository, times(1)).deleteById(reviewId);
    }

    @Test
    void testUpdateReview_success() {
        UUID reviewId = UUID.randomUUID();
        VendorReview review = VendorReview.builder().id(reviewId).build();
        VendorReviewRequestDTO dto = new VendorReviewRequestDTO();
        dto.setComment("Updated");
        dto.setRating(5.0);

        when(vendorReviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        vendorReviewService.updateReview(reviewId, dto);

        assertEquals("Updated", review.getComment());
        assertEquals(5.0, review.getRating());
        verify(vendorReviewRepository).save(review);
    }

    @Test
    void testUpdateReview_notFound() {
        UUID reviewId = UUID.randomUUID();
        VendorReviewRequestDTO dto = new VendorReviewRequestDTO();

        when(vendorReviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            vendorReviewService.updateReview(reviewId, dto);
        });
    }
}
