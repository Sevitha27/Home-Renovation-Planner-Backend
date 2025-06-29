package com.lowes.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorReviewDTO {
    private UUID id;
    private String name;      // From vendor.user.name
    private String pic;       // From vendor.user.pic
    private Double rating;    // Average of all ratings
    private List<String> reviews;
    private boolean available; // List of comment strings
}
