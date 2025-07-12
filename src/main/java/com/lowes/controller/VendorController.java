package com.lowes.controller;


import com.lowes.dto.request.vendor.QuoteUpdateRequestDTO;
import com.lowes.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/vendor")
@RestController
public class VendorController {
    private final VendorService vendorService;

    //get phases
    @GetMapping("/phases")
    @PreAuthorize("hasRole('ROLE_VENDOR')")
    public ResponseEntity<?> getAssignedPhases() {
        return vendorService.getAssignedPhases();
    }

    //send quote
    @PostMapping("/phase/{phaseId}/quote")
    @PreAuthorize("hasRole('ROLE_VENDOR')")
    public ResponseEntity<?> submitQuote(@PathVariable UUID phaseId, @RequestBody QuoteUpdateRequestDTO dto) {
        return vendorService.submitQuote(phaseId, dto);
    }

    //get approval status
    @GetMapping("/getVendorDetails")
    @PreAuthorize("hasRole('ROLE_VENDOR')")
    public ResponseEntity<?> getVendorApprovalStatus(){
        return vendorService.getVendorApprovalStatus();
    }

}
