package com.lowes.service;

import com.lowes.dto.request.vendor.QuoteUpdateRequestDTO;
import com.lowes.dto.response.admin.AdminToastDTO;
import com.lowes.dto.response.auth.UserResponseDTO;
import com.lowes.dto.response.vendor.PhaseResponseDTO;
import com.lowes.entity.Phase;
import com.lowes.entity.PhaseMaterial;
import com.lowes.entity.User;
import com.lowes.entity.Vendor;
import com.lowes.mapper.VendorMapper;
import com.lowes.repository.PhaseRepository;
import com.lowes.repository.VendorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VendorService {

    private final VendorRepository vendorRepository;
    private final PhaseRepository phaseRepository;
    private final VendorMapper vendorMapper;

    public ResponseEntity<?> getAssignedPhases() {
        try
        {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Vendor vendor = vendorRepository.findByUser(user);
            List<Phase> phases = phaseRepository.findByVendor(vendor);
            return ResponseEntity.status(HttpStatus.OK).body(vendorMapper.toPhaseResponseDTOList(phases));
        }catch(Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }

    }

    @Transactional
    public ResponseEntity<?> submitQuote(UUID phaseId, QuoteUpdateRequestDTO dto) {
        try{
            Optional<Phase> phaseOpt = phaseRepository.findById(phaseId);
            if(phaseOpt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(AdminToastDTO.builder().message("ERROR").build());
            Phase phase=phaseOpt.get();
            phase.setVendorCost(dto.getVendorCost());
            phaseRepository.save(phase);
            return ResponseEntity.status(HttpStatus.OK).body(AdminToastDTO.builder().message("SUCCESS").build());
        }catch(Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(AdminToastDTO.builder().message("ERROR").build());
        }

    }
}
