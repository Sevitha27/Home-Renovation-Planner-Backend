package com.lowes.dto.request;

import lombok.Data;
import java.util.UUID;

@Data
public class VendorReviewRequestDTO {
    private UUID vendorId;
    private UUID userId;
    private UUID phaseId; // optional if linked to a phase
    private String comment;
    private double rating;
}
