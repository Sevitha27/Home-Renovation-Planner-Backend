package com.lowes.service;

import com.lowes.entity.Vendor;
import com.lowes.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class VendorAssignmentService {

    @Autowired
    private VendorRepository vendorRepository;

    public void setAvailability(UUID vendorId, boolean availability) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        vendor.setAvailable(false); // mark unavailable
        vendorRepository.save(vendor);
    }

}