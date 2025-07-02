package com.lowes.controller;

import com.lowes.service.VendorAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/vendor-assignment")
public class VendorAssignmentController {

    @Autowired
    private VendorAssignmentService vendorAssignmentService;

    // VendorAssignmentController.java
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CUSTOMER')")
    @PutMapping("/assign/{vendorId}")
    public ResponseEntity<?> assignVendor(@PathVariable UUID vendorId) {
        return ResponseEntity.ok("Assigned");
    }
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CUSTOMER')")
    @PutMapping("/unassign/{vendorId}")
    public ResponseEntity<?> unassignVendor(@PathVariable UUID vendorId) {

        return ResponseEntity.ok("Unassigned");
    }


}