package com.lowes.controller;

import com.lowes.service.VendorAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/vendor-assignment")
@CrossOrigin(origins = "http://localhost:5173") // or whatever your frontend port is

public class VendorAssignmentController {

    @Autowired
    private VendorAssignmentService vendorAssignmentService;

    @PutMapping("/assign/{vendorId}")
    public String assignVendor(@PathVariable UUID vendorId) {
        vendorAssignmentService.assignVendor(vendorId);
        return "Vendor assigned and marked unavailable.";
    }


}
