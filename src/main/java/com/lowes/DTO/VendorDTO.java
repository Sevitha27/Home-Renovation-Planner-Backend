package com.lowes.dto;

import com.lowes.entity.Vendor; // Import Vendor entity to map from it
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VendorDTO {
    private UUID exposedId;
    private String companyName;
    // Add any other vendor fields you want to expose to the frontend (e.g., contactPerson, email)

    // Constructor to convert a Vendor entity to VendorDTO
    public VendorDTO(Vendor vendor) {
        if (vendor != null) {
            this.exposedId = vendor.getExposedId();
            this.companyName = vendor.getCompanyName(); // Assuming your Vendor entity has getCompanyName()
            // Map other fields as needed
        }
    }
}