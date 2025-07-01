package com.lowes.controller;

import com.lowes.dto.request.VendorReviewRequestDTO;
import com.lowes.dto.response.VendorReviewDTO;
import com.lowes.service.VendorReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vendor-reviews")
@CrossOrigin(origins = "http://localhost:5173") // or whatever your frontend port is

public class VendorReviewController {

    @Autowired
    private VendorReviewService vendorReviewService;

    @GetMapping("/by-phaseType")
    public ResponseEntity<List<VendorReviewDTO>> getVendorsBySkill(@RequestParam String phaseType) {
        List<VendorReviewDTO> vendors = vendorReviewService.getVendorsBySkill(phaseType);
        return ResponseEntity.ok(vendors);
    }
    @PostMapping("/reviews")
    public ResponseEntity<String> addReview(@RequestBody VendorReviewRequestDTO dto) {
        System.out.println(dto);
        vendorReviewService.addReview(dto);
        return ResponseEntity.ok("Review added successfully.");
    }

    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<String> updateReview(@PathVariable UUID reviewId, @RequestBody VendorReviewRequestDTO dto) {
        vendorReviewService.updateReview(reviewId, dto);
        return ResponseEntity.ok("Review updated successfully.");
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable UUID reviewId) {
        vendorReviewService.deleteReview(reviewId);
        return ResponseEntity.ok("Review deleted successfully.");
    }



}