package com.lowes.controller;


import com.lowes.dto.request.vendor.QuoteUpdateRequestDTO;
import com.lowes.dto.response.vendor.PhaseResponseDTO;
import com.lowes.entity.User;
import com.lowes.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

}
