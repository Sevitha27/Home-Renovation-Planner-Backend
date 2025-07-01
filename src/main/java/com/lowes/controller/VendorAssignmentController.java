package com.lowes.controller;

import com.lowes.service.VendorAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/vendor-assignment")
@CrossOrigin(origins = "http://localhost:5173") // or whatever your frontend port is

public class VendorAssignmentController {

    @Autowired
    private VendorAssignmentService vendorAssignmentService;

    // VendorAssignmentController.java
    @PutMapping("/assign/{vendorId}")
    public ResponseEntity<?> assignVendor(@PathVariable UUID vendorId) {
        return ResponseEntity.ok("Assigned");
    }

    @PutMapping("/unassign/{vendorId}")
    public ResponseEntity<?> unassignVendor(@PathVariable UUID vendorId) {

        return ResponseEntity.ok("Unassigned");
    }


}