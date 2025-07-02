package com.lowes.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class VendorReviewDTO {
    private UUID id;
    private String name;
    private String pic;
    private String experience;        // in years
    private String companyName;
    private double rating;
    private boolean available;
    private Double basePrice;
    private List<ReviewDetail> reviews;


    @Data
    @Builder
    public static class ReviewDetail {
        private String reviewerName;
        private double rating;
        private String comment;
        private String createdAt; // ISO string or formatted date
    }
}