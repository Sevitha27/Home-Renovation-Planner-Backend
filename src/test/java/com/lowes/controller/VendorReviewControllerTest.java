package com.lowes.controller;

import com.lowes.dto.request.VendorReviewRequestDTO;
import com.lowes.service.VendorReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VendorReviewControllerTest {

    @InjectMocks
    private VendorReviewController vendorReviewController;

    @Mock
    private VendorReviewService vendorReviewService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Necessary for pure unit tests
    }

    @Test
    void testAddReview() {
        VendorReviewRequestDTO dto = new VendorReviewRequestDTO();
        dto.setComment("Nice");
        dto.setRating(4.0);
        dto.setUserId(UUID.randomUUID());
        dto.setVendorId(UUID.randomUUID());

        ResponseEntity<String> response = vendorReviewController.addReview(dto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Review added successfully.", response.getBody());
        verify(vendorReviewService, times(1)).addReview(dto);
    }

    @Test
    void testDeleteReview() {
        UUID id = UUID.randomUUID();

        ResponseEntity<String> response = vendorReviewController.deleteReview(id);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Review deleted successfully.", response.getBody());
        verify(vendorReviewService, times(1)).deleteReview(id);
    }

    @Test
    void testGetVendorsBySkill() {
        when(vendorReviewService.getVendorsBySkill("PAINTING")).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = vendorReviewController.getVendorsBySkill("PAINTING");

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof Iterable);
    }
}
