package com.lowes.service;

import com.lowes.dto.request.VendorReviewRequestDTO;
import com.lowes.dto.response.VendorReviewDTO;
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

    @Test
    void testGetVendorsBySkill_success() {
        SkillType skillType = SkillType.PLUMBING;
        Skill skill = Skill.builder()
                .name(skillType)
                .basePrice(1500.0)
                .build();

        Vendor vendor = Vendor.builder()
                .available(true)
                .approved(true)
                .experience("5")
                .companyName("PlumbCo")
                .reviews(List.of())
                .user(User.builder().name("VendorUser").pic("pic.jpg").build())
                .exposedId(UUID.randomUUID())
                .build();

        // 🔥 Important: link vendor and skill both ways
        vendor.setSkills(List.of(skill));
        skill.setVendors(List.of(vendor));

        when(skillRepository.findByName(skillType)).thenReturn(List.of(skill));

        List<VendorReviewDTO> result = vendorReviewService.getVendorsBySkill("PLUMBING");

        assertEquals(1, result.size());
        assertEquals("VendorUser", result.get(0).getName());
        assertEquals(1500.0, result.get(0).getBasePrice());
    }

    @Test
    void testGetVendorsBySkill_invalidSkill() {
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                vendorReviewService.getVendorsBySkill("invalid-skill")
        );

        assertTrue(ex.getMessage().contains("Invalid skill"));
    }

    @Test
    void testGetVendorsBySkill_skillNotFound() {
        when(skillRepository.findByName(SkillType.CARPENTRY)).thenReturn(Collections.emptyList());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                vendorReviewService.getVendorsBySkill("CARPENTRY")
        );

        assertTrue(ex.getMessage().contains("Skill not found"));
    }

    @Test
    void testGetVendorsBySkill_noVendorsForSkill() {
        Skill skill = Skill.builder()
                .name(SkillType.ELECTRICAL)
                .vendors(Collections.emptyList()) // no vendors
                .basePrice(1000.0)
                .build();

        when(skillRepository.findByName(SkillType.ELECTRICAL)).thenReturn(List.of(skill));

        List<VendorReviewDTO> result = vendorReviewService.getVendorsBySkill("ELECTRICAL");

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetVendorsBySkill_vendorNotApproved() {
        Skill skill = Skill.builder().name(SkillType.TILING).basePrice(999.0).build();
        Vendor vendor = Vendor.builder()
                .available(true)
                .approved(false) // ❌ not approved
                .skills(List.of(skill))
                .reviews(List.of())
                .user(User.builder().name("FakeVendor").pic("img.png").build())
                .exposedId(UUID.randomUUID())
                .build();
        skill.setVendors(List.of(vendor));

        when(skillRepository.findByName(SkillType.TILING)).thenReturn(List.of(skill));

        List<VendorReviewDTO> result = vendorReviewService.getVendorsBySkill("TILING");

        assertTrue(result.isEmpty()); // should be filtered out
    }

    @Test
    void testGetVendorsBySkill_withReviews_averageRating() {
        Skill skill = Skill.builder().name(SkillType.ELECTRICAL).basePrice(1800.0).build();

        User reviewer = User.builder().name("Customer1").build();
        VendorReview review1 = VendorReview.builder().reviewer(reviewer).rating(4.0).comment("Good").build();
        VendorReview review2 = VendorReview.builder().reviewer(reviewer).rating(5.0).comment("Great").build();

        Vendor vendor = Vendor.builder()
                .available(true)
                .approved(true)
                .skills(List.of(skill))
                .reviews(List.of(review1, review2))
                .user(User.builder().name("TopVendor").pic("top.jpg").build())
                .exposedId(UUID.randomUUID())
                .experience("10")
                .companyName("TopCo")
                .build();

        skill.setVendors(List.of(vendor));

        when(skillRepository.findByName(SkillType.ELECTRICAL)).thenReturn(List.of(skill));

        List<VendorReviewDTO> result = vendorReviewService.getVendorsBySkill("ELECTRICAL");

        assertEquals(1, result.size());
        assertEquals(4.5, result.get(0).getRating()); // average
        assertEquals("TopVendor", result.get(0).getName());
        assertEquals(2, result.get(0).getReviews().size());
    }


    @Test
    void testGetVendorsBySkill_vendorNotAvailable() {
        Skill skill = Skill.builder().name(SkillType.CARPENTRY).basePrice(1200.0).build();
        Vendor vendor = Vendor.builder()
                .available(false) // ❌ not available
                .approved(true)
                .skills(List.of(skill))
                .reviews(List.of())
                .user(User.builder().name("BlockedVendor").pic("pic.png").build())
                .exposedId(UUID.randomUUID())
                .build();
        skill.setVendors(List.of(vendor));

        when(skillRepository.findByName(SkillType.CARPENTRY)).thenReturn(List.of(skill));

        List<VendorReviewDTO> result = vendorReviewService.getVendorsBySkill("CARPENTRY");

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetVendorsBySkill_vendorWithEmptyReviewsList() {
        Skill skill = Skill.builder().name(SkillType.PLUMBING).basePrice(1100.0).build();

        Vendor vendor = Vendor.builder()
                .available(true)
                .approved(true)
                .skills(List.of(skill))
                .reviews(new ArrayList<>()) // 👈 Empty list, not null (to avoid NPE)
                .user(User.builder().name("EmptyReviewVendor").pic("img.png").build())
                .exposedId(UUID.randomUUID())
                .experience("3")
                .companyName("Reviewless Inc.")
                .build();

        skill.setVendors(List.of(vendor));
        when(skillRepository.findByName(SkillType.PLUMBING)).thenReturn(List.of(skill));

        List<VendorReviewDTO> result = vendorReviewService.getVendorsBySkill("PLUMBING");

        assertEquals(1, result.size());
        assertEquals("EmptyReviewVendor", result.get(0).getName());
        assertEquals(0.0, result.get(0).getRating()); // Should default to 0
        assertTrue(result.get(0).getReviews().isEmpty()); // No reviews present
    }

    @Test
    void testGetVendorsBySkill_mixedVendors() {
        Skill skill = Skill.builder().name(SkillType.TILING).basePrice(1700.0).build();

        Vendor approvedAvailable = Vendor.builder()
                .approved(true).available(true)
                .skills(List.of(skill))
                .reviews(new ArrayList<>())
                .user(User.builder().name("GoodVendor").pic("good.jpg").build())
                .exposedId(UUID.randomUUID())
                .experience("6").companyName("Good Co").build();

        Vendor approvedNotAvailable = Vendor.builder()
                .approved(true).available(false) // filtered
                .skills(List.of(skill))
                .reviews(new ArrayList<>())
                .user(User.builder().name("UnavailableVendor").pic("u.jpg").build())
                .exposedId(UUID.randomUUID()).build();

        Vendor notApprovedAvailable = Vendor.builder()
                .approved(false).available(true) // filtered
                .skills(List.of(skill))
                .reviews(new ArrayList<>())
                .user(User.builder().name("UnapprovedVendor").pic("x.jpg").build())
                .exposedId(UUID.randomUUID()).build();

        skill.setVendors(List.of(approvedAvailable, approvedNotAvailable, notApprovedAvailable));
        when(skillRepository.findByName(SkillType.TILING)).thenReturn(List.of(skill));

        List<VendorReviewDTO> result = vendorReviewService.getVendorsBySkill("TILING");

        assertEquals(1, result.size());
        assertEquals("GoodVendor", result.get(0).getName());
    }

    @Test
    void testAddReview_nullComment() {
        UUID vendorId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Vendor vendor = Vendor.builder().exposedId(vendorId).reviews(new ArrayList<>()).build();
        User user = User.builder().exposedId(userId).build();

        VendorReviewRequestDTO dto = new VendorReviewRequestDTO();
        dto.setVendorId(vendorId);
        dto.setUserId(userId);
        dto.setRating(4.0);
        dto.setComment(null); // comment is optional

        when(vendorRepository.findByExposedId(vendorId)).thenReturn(vendor);
        when(userRepository.findByExposedId(userId)).thenReturn(user);

        vendorReviewService.addReview(dto);

        verify(vendorReviewRepository).save(any(VendorReview.class));
    }

    @Test
    void testGetVendorsBySkill_vendorWithNullReviews_shouldThrow() {
        Skill skill = Skill.builder().name(SkillType.PAINTING).basePrice(1400.0).build();

        Vendor vendor = Vendor.builder()
                .approved(true).available(true)
                .skills(List.of(skill))
                .reviews(null) // 💥 simulate risky case
                .user(User.builder().name("NullReviewVendor").pic("n.png").build())
                .exposedId(UUID.randomUUID())
                .build();

        skill.setVendors(List.of(vendor));
        when(skillRepository.findByName(SkillType.PAINTING)).thenReturn(List.of(skill));

        assertThrows(NullPointerException.class, () ->
                vendorReviewService.getVendorsBySkill("PAINTING")
        );
    }

    @Test
    void testUpdateReview_allowsInvalidRatingBecauseNoValidationInService() {
        UUID reviewId = UUID.randomUUID();
        VendorReview review = VendorReview.builder().id(reviewId).build();
        VendorReviewRequestDTO dto = new VendorReviewRequestDTO();
        dto.setComment("Updated comment");
        dto.setRating(7.0); // Invalid logically, but service doesn't validate

        when(vendorReviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        vendorReviewService.updateReview(reviewId, dto);

        assertEquals("Updated comment", review.getComment());
        assertEquals(7.0, review.getRating()); // Accepted as valid in current service
        verify(vendorReviewRepository).save(review);
    }

    @Test
    void testGetVendorsBySkill_vendorNotApprovedAndNotAvailable() {
        Skill skill = Skill.builder()
                .name(SkillType.PLUMBING) // ✅ Use valid enum
                .basePrice(1000.0)
                .build();

        Vendor vendor = Vendor.builder()
                .approved(false)           // ❌ not approved
                .available(false)          // ❌ not available
                .skills(List.of(skill))
                .reviews(new ArrayList<>()) // ✅ safe, avoids NPE
                .user(User.builder()
                        .name("BlockedVendor")
                        .pic("blocked.jpg")
                        .build())
                .exposedId(UUID.randomUUID())
                .experience("0")
                .companyName("Blocked Co")
                .build();

        // Link vendor to skill
        skill.setVendors(List.of(vendor));

        // Mock repository call
        when(skillRepository.findByName(SkillType.PLUMBING)).thenReturn(List.of(skill));

        // Call service
        List<VendorReviewDTO> result = vendorReviewService.getVendorsBySkill("PLUMBING");

        // Expect the vendor to be skipped
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetVendorsBySkill_vendorWithNullUser_shouldThrow() {
        Skill skill = Skill.builder().name(SkillType.PAINTING).basePrice(1100.0).build();

        Vendor vendor = Vendor.builder()
                .approved(true).available(true)
                .skills(List.of(skill))
                .reviews(new ArrayList<>())
                .user(null) // 💥 null user
                .exposedId(UUID.randomUUID()).build();

        skill.setVendors(List.of(vendor));
        when(skillRepository.findByName(SkillType.PAINTING)).thenReturn(List.of(skill));

        assertThrows(NullPointerException.class, () -> vendorReviewService.getVendorsBySkill("PAINTING"));
    }

    @Test
    void testGetVendorsBySkill_vendorWithNullExperienceCompanyName() {
        Skill skill = Skill.builder().name(SkillType.CARPENTRY).basePrice(1300.0).build();

        Vendor vendor = Vendor.builder()
                .approved(true).available(true)
                .skills(List.of(skill))
                .reviews(new ArrayList<>())
                .user(User.builder().name("NullExpVendor").pic("null.png").build())
                .exposedId(UUID.randomUUID())
                .experience(null)
                .companyName(null)
                .build();

        skill.setVendors(List.of(vendor));
        when(skillRepository.findByName(SkillType.CARPENTRY)).thenReturn(List.of(skill));

        List<VendorReviewDTO> result = vendorReviewService.getVendorsBySkill("CARPENTRY");

        assertEquals(1, result.size());
        assertNull(result.get(0).getExperience());
        assertNull(result.get(0).getCompanyName());
    }

    @Test
    void testAddReview_vendorNotFound_shouldThrow() {
        UUID vendorId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        VendorReviewRequestDTO dto = new VendorReviewRequestDTO();
        dto.setVendorId(vendorId);
        dto.setUserId(userId);
        dto.setRating(4.0);
        dto.setComment("Great");

        when(vendorRepository.findByExposedId(vendorId)).thenReturn(null); // 💥

        assertThrows(RuntimeException.class, () -> vendorReviewService.addReview(dto));
    }

    @Test
    void testAddReview_userNotFound_shouldThrow() {
        UUID vendorId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        VendorReviewRequestDTO dto = new VendorReviewRequestDTO();
        dto.setVendorId(vendorId);
        dto.setUserId(userId);
        dto.setRating(4.0);
        dto.setComment("Awesome");

        when(vendorRepository.findByExposedId(vendorId)).thenReturn(Vendor.builder().exposedId(vendorId).reviews(new ArrayList<>()).build());
        when(userRepository.findByExposedId(userId)).thenReturn(null); // 💥

        assertThrows(RuntimeException.class, () -> vendorReviewService.addReview(dto));
    }

    @Test
    void testUpdateReview_onlyRatingUpdated() {
        UUID reviewId = UUID.randomUUID();
        VendorReview review = VendorReview.builder()
                .id(reviewId)
                .comment("Old comment")
                .rating(3.0)
                .build();

        VendorReviewRequestDTO dto = new VendorReviewRequestDTO();
        dto.setRating(4.8);
        dto.setComment(null); // comment will be overwritten to null

        when(vendorReviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        vendorReviewService.updateReview(reviewId, dto);

        // Expectation must match current logic
        assertNull(review.getComment()); // ❗ Expect null
        assertEquals(4.8, review.getRating());
    }


    @Test
    void testGetVendorsBySkill_vendorWithEmptyNameAndPic() {
        Skill skill = Skill.builder().name(SkillType.ELECTRICAL).basePrice(1800.0).build();

        Vendor vendor = Vendor.builder()
                .approved(true).available(true)
                .skills(List.of(skill))
                .reviews(new ArrayList<>())
                .user(User.builder().name("").pic("").build()) // edge case
                .exposedId(UUID.randomUUID()).build();

        skill.setVendors(List.of(vendor));
        when(skillRepository.findByName(SkillType.ELECTRICAL)).thenReturn(List.of(skill));

        List<VendorReviewDTO> result = vendorReviewService.getVendorsBySkill("ELECTRICAL");

        assertEquals(1, result.size());
        assertEquals("", result.get(0).getName());
        assertEquals("", result.get(0).getPic());
    }

    @Test
    void testUpdateReview_onlyCommentUpdated() {
        UUID reviewId = UUID.randomUUID();
        VendorReview review = VendorReview.builder()
                .id(reviewId)
                .comment("Old comment")
                .rating(3.0)
                .build();

        VendorReviewRequestDTO dto = new VendorReviewRequestDTO();
        dto.setComment("New comment");
        dto.setRating(0.0);  // This will update rating to 0.0 per current service logic

        when(vendorReviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        vendorReviewService.updateReview(reviewId, dto);

        assertEquals("New comment", review.getComment());
        assertEquals(0.0, review.getRating());  // updated to 0.0 as per current logic
    }


    @Test
    void testUpdateReview_ratingZero_commentNull() {
        UUID reviewId = UUID.randomUUID();
        VendorReview review = VendorReview.builder()
                .id(reviewId)
                .comment("Old comment")
                .rating(3.0)
                .build();

        VendorReviewRequestDTO dto = new VendorReviewRequestDTO();
        dto.setComment(null);
        dto.setRating(0.0);

        when(vendorReviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        vendorReviewService.updateReview(reviewId, dto);

        assertNull(review.getComment());          // comment set to null per service
        assertEquals(0.0, review.getRating());    // rating updated to 0.0 per service
    }


    @Test
    void testAddReview_ratingAtBounds() {
        UUID vendorId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Vendor vendor = Vendor.builder().exposedId(vendorId).reviews(new ArrayList<>()).build();
        User user = User.builder().exposedId(userId).build();

        VendorReviewRequestDTO dtoLow = new VendorReviewRequestDTO();
        dtoLow.setVendorId(vendorId);
        dtoLow.setUserId(userId);
        dtoLow.setRating(1.0);
        dtoLow.setComment("Lowest rating");

        VendorReviewRequestDTO dtoHigh = new VendorReviewRequestDTO();
        dtoHigh.setVendorId(vendorId);
        dtoHigh.setUserId(userId);
        dtoHigh.setRating(5.0);
        dtoHigh.setComment("Highest rating");

        when(vendorRepository.findByExposedId(vendorId)).thenReturn(vendor);
        when(userRepository.findByExposedId(userId)).thenReturn(user);

        vendorReviewService.addReview(dtoLow);
        vendorReviewService.addReview(dtoHigh);

        verify(vendorReviewRepository, times(2)).save(any(VendorReview.class));
    }

    @Test
    void testUpdateReview_emptyComment() {
        UUID reviewId = UUID.randomUUID();
        VendorReview review = VendorReview.builder()
                .id(reviewId)
                .comment("Old comment")
                .rating(3.0)
                .build();

        VendorReviewRequestDTO dto = new VendorReviewRequestDTO();
        dto.setComment(""); // empty string
        dto.setRating(3.5);

        when(vendorReviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        vendorReviewService.updateReview(reviewId, dto);

        assertEquals("", review.getComment());
        assertEquals(3.5, review.getRating());
        verify(vendorReviewRepository).save(review);
    }

    @Test
    void testUpdateReview_negativeRating() {
        UUID reviewId = UUID.randomUUID();
        VendorReview review = VendorReview.builder()
                .id(reviewId)
                .comment("Comment")
                .rating(4.0)
                .build();

        VendorReviewRequestDTO dto = new VendorReviewRequestDTO();
        dto.setComment("Updated comment");
        dto.setRating(-1.0);  // negative rating, no validation in service

        when(vendorReviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        vendorReviewService.updateReview(reviewId, dto);

        assertEquals("Updated comment", review.getComment());
        assertEquals(-1.0, review.getRating());
        verify(vendorReviewRepository).save(review);
    }

    @Test
    void testUpdateReview_ratingAtLowerBound() {
        UUID reviewId = UUID.randomUUID();
        VendorReview review = VendorReview.builder()
                .id(reviewId)
                .comment("Old comment")
                .rating(2.5)
                .build();

        VendorReviewRequestDTO dto = new VendorReviewRequestDTO();
        dto.setComment("Lower bound rating");
        dto.setRating(1.0);  // rating exactly 1

        when(vendorReviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        vendorReviewService.updateReview(reviewId, dto);

        assertEquals("Lower bound rating", review.getComment());
        assertEquals(1.0, review.getRating());
        verify(vendorReviewRepository).save(review);
    }

    @Test
    void testUpdateReview_ratingAtUpperBound() {
        UUID reviewId = UUID.randomUUID();
        VendorReview review = VendorReview.builder()
                .id(reviewId)
                .comment("Old comment")
                .rating(4.0)
                .build();

        VendorReviewRequestDTO dto = new VendorReviewRequestDTO();
        dto.setComment("Upper bound rating");
        dto.setRating(5.0);  // rating exactly 5

        when(vendorReviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        vendorReviewService.updateReview(reviewId, dto);

        assertEquals("Upper bound rating", review.getComment());
        assertEquals(5.0, review.getRating());
        verify(vendorReviewRepository).save(review);
    }

    @Test
    void testUpdateReview_largeRatingValue() {
        UUID reviewId = UUID.randomUUID();
        VendorReview review = VendorReview.builder()
                .id(reviewId)
                .comment("Old comment")
                .rating(3.0)
                .build();

        VendorReviewRequestDTO dto = new VendorReviewRequestDTO();
        dto.setComment("Large rating");
        dto.setRating(1000.0);  // extremely large rating, no validation

        when(vendorReviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        vendorReviewService.updateReview(reviewId, dto);

        assertEquals("Large rating", review.getComment());
        assertEquals(1000.0, review.getRating());
        verify(vendorReviewRepository).save(review);
    }

    @Test
    void testGetVendorsBySkill_invalidSkillFormat_throws() {
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                vendorReviewService.getVendorsBySkill("not-a-valid-skill")
        );
        assertTrue(ex.getMessage().contains("Invalid skill"));
    }

    @Test
    void testGetVendorsBySkill_emptySkillList_throws() {
        when(skillRepository.findByName(SkillType.PLUMBING)).thenReturn(Collections.emptyList());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                vendorReviewService.getVendorsBySkill("PLUMBING")
        );
        assertTrue(ex.getMessage().contains("Skill not found"));
    }

    @Test
    void testUpdateReview_reviewNotFound_throws() {
        UUID reviewId = UUID.randomUUID();

        when(vendorReviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                vendorReviewService.updateReview(reviewId, new VendorReviewRequestDTO())
        );
        assertEquals("Review not found", ex.getMessage());
    }

    @Test
    void testAddReview_vendorNotFound_throws() {
        UUID vendorId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        VendorReviewRequestDTO dto = new VendorReviewRequestDTO();
        dto.setVendorId(vendorId);
        dto.setUserId(userId);
        dto.setRating(4.0);
        dto.setComment("Great");

        when(vendorRepository.findByExposedId(vendorId)).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                vendorReviewService.addReview(dto)
        );
        assertTrue(ex.getMessage().contains("Vendor not found"));
    }

    @Test
    void testAddReview_userNotFound_throws() {
        UUID vendorId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        VendorReviewRequestDTO dto = new VendorReviewRequestDTO();
        dto.setVendorId(vendorId);
        dto.setUserId(userId);
        dto.setRating(4.0);
        dto.setComment("Great");

        when(vendorRepository.findByExposedId(vendorId)).thenReturn(Vendor.builder().exposedId(vendorId).build());
        when(userRepository.findByExposedId(userId)).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                vendorReviewService.addReview(dto)
        );
        assertTrue(ex.getMessage().contains("User not found"));
    }
    @Test
    void testMapToDTO_withNullFields() {
        Skill skill = Skill.builder()
                .name(SkillType.PAINTING)
                .basePrice(1400.0)
                .build();

        Vendor vendor = Vendor.builder()
                .approved(true)
                .available(true)
                .skills(List.of(skill))
                .reviews(Collections.emptyList())  // avoid null to prevent NPE in stream()
                .user(User.builder()
                        .name("Test User")           // provide dummy non-null user
                        .pic(null)                   // pic can be null if you want to test that
                        .build())
                .exposedId(UUID.randomUUID())
                .experience(null)                   // null fields to test behavior
                .companyName(null)
                .build();

        skill.setVendors(List.of(vendor));

        // Call mapToDTO via getVendorsBySkill or reflection since it is private
        // Using reflection here to test private method directly:
        try {
            java.lang.reflect.Method method = VendorReviewService.class.getDeclaredMethod("mapToDTO", Vendor.class);
            method.setAccessible(true);
            VendorReviewDTO dto = (VendorReviewDTO) method.invoke(vendorReviewService, vendor);

            assertNotNull(dto);
            assertEquals("Test User", dto.getName());
            assertNull(dto.getPic());  // pic was null
            assertEquals(0.0, dto.getRating()); // no reviews means average rating 0.0
            assertTrue(dto.getReviews().isEmpty()); // empty reviews list
            assertNull(dto.getExperience());
            assertNull(dto.getCompanyName());
            assertEquals(1400.0, dto.getBasePrice());
            assertTrue(dto.isAvailable());
        } catch (Exception e) {
            fail("mapToDTO threw exception: " + e.getCause());
        }
    }

    @Test
    void testAddReview_ratingTooLow_throwsException() {
        VendorReviewRequestDTO dto = new VendorReviewRequestDTO();
        dto.setRating(0.5); // invalid low rating
        dto.setVendorId(UUID.randomUUID());
        dto.setUserId(UUID.randomUUID());

        when(vendorRepository.findByExposedId(dto.getVendorId()))
                .thenReturn(new Vendor());
        when(userRepository.findByExposedId(dto.getUserId()))
                .thenReturn(new User());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            vendorReviewService.addReview(dto);
        });
        assertEquals("Rating must be between 1 and 5", ex.getMessage());
    }

    @Test
    void testAddReview_ratingTooHigh_throwsException() {
        VendorReviewRequestDTO dto = new VendorReviewRequestDTO();
        dto.setRating(6.0); // invalid high rating
        dto.setVendorId(UUID.randomUUID());
        dto.setUserId(UUID.randomUUID());

        when(vendorRepository.findByExposedId(dto.getVendorId()))
                .thenReturn(new Vendor());
        when(userRepository.findByExposedId(dto.getUserId()))
                .thenReturn(new User());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            vendorReviewService.addReview(dto);
        });
        assertEquals("Rating must be between 1 and 5", ex.getMessage());
    }
    @Test
    void testGetVendorsBySkill_reviewWithNullCreatedAt() {
        Skill skill = Skill.builder().name(SkillType.PLUMBING).basePrice(1000.0).build();

        User reviewer = User.builder().name("John Doe").build();

        VendorReview reviewWithNullCreatedAt = VendorReview.builder()
                .reviewer(reviewer)
                .rating(4.0)
                .comment("Nice job")
                .createdAt(null)  // Explicitly null here
                .build();

        Vendor vendor = Vendor.builder()
                .approved(true)
                .available(true)
                .skills(List.of(skill))
                .reviews(List.of(reviewWithNullCreatedAt))
                .user(User.builder().name("VendorUser").pic("pic.jpg").build())
                .exposedId(UUID.randomUUID())
                .build();

        skill.setVendors(List.of(vendor));
        when(skillRepository.findByName(SkillType.PLUMBING)).thenReturn(List.of(skill));

        List<VendorReviewDTO> dtos = vendorReviewService.getVendorsBySkill("PLUMBING");

        assertEquals(1, dtos.size());
        VendorReviewDTO vendorDTO = dtos.get(0);
        assertEquals(1, vendorDTO.getReviews().size());
        assertEquals("", vendorDTO.getReviews().get(0).getCreatedAt());  // should be empty string when createdAt is null
    }
    @Test
    void testGetVendorsBySkill_withVendorNoSkillsAndReviewWithNullCreatedAt() {
        Skill skill = Skill.builder()
                .name(SkillType.PLUMBING)
                .basePrice(1000.0)
                .build();

        User reviewer = User.builder().name("ReviewerName").build();
        VendorReview reviewWithNullCreatedAt = VendorReview.builder()
                .reviewer(reviewer)
                .rating(4.5)
                .comment("Nice job")
                .createdAt(null)
                .build();

        Vendor vendorWithNoSkills = Vendor.builder()
                .approved(true)
                .available(true)
                .skills(Collections.emptyList())  // no skills to cover basePrice null
                .reviews(List.of(reviewWithNullCreatedAt))
                .user(User.builder().name("VendorUser").pic("pic.jpg").build())
                .exposedId(UUID.randomUUID())
                .experience("5 years")
                .companyName("Company ABC")
                .build();

        skill.setVendors(List.of(vendorWithNoSkills));

        when(skillRepository.findByName(SkillType.PLUMBING)).thenReturn(List.of(skill));

        List<VendorReviewDTO> dtos = vendorReviewService.getVendorsBySkill("PLUMBING");

        assertEquals(1, dtos.size());
        VendorReviewDTO dto = dtos.get(0);

        assertNull(dto.getBasePrice());  // skill == null so basePrice null
        assertEquals("VendorUser", dto.getName());
        assertEquals("5 years", dto.getExperience());
        assertEquals("Company ABC", dto.getCompanyName());

        assertEquals(1, dto.getReviews().size());
        VendorReviewDTO.ReviewDetail reviewDetail = dto.getReviews().get(0);
        assertEquals("ReviewerName", reviewDetail.getReviewerName());
        assertEquals(4.5, reviewDetail.getRating());
        assertEquals("Nice job", reviewDetail.getComment());
        assertEquals("", reviewDetail.getCreatedAt()); // null createdAt case
    }



}