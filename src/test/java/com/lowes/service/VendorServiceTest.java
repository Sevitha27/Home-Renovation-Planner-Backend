package com.lowes.service;

import com.lowes.dto.request.vendor.QuoteUpdateRequestDTO;
import com.lowes.dto.response.admin.AdminToastDTO;
import com.lowes.entity.Phase;
import com.lowes.entity.User;
import com.lowes.entity.Vendor;
import com.lowes.mapper.VendorMapper;
import com.lowes.repository.PhaseRepository;
import com.lowes.repository.VendorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class VendorServiceTest {
    @Mock
    private VendorRepository vendorRepository;
    @Mock
    private PhaseRepository phaseRepository;
    @Mock
    private VendorMapper vendorMapper;
    @InjectMocks
    private VendorService vendorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // getAssignedPhases
    @Test
    void testGetAssignedPhases_success() {
        User user = new User();
        Vendor vendor = new Vendor();
        List<Phase> phases = List.of(new Phase());
        when(vendorRepository.findByUser(any(User.class))).thenReturn(vendor);
        when(phaseRepository.findByVendor(any(Vendor.class))).thenReturn(phases);
        when(vendorMapper.toPhaseResponseDTOList(anyList())).thenReturn(List.of());
        mockSecurityContext(user);
        ResponseEntity<?> response = vendorService.getAssignedPhases();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
    @Test
    void testGetAssignedPhases_exception() {
        mockSecurityContext(null);
        when(vendorRepository.findByUser(any())).thenThrow(new RuntimeException());
        ResponseEntity<?> response = vendorService.getAssignedPhases();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    // submitQuote
    @Test
    void testSubmitQuote_success() {
        UUID phaseId = UUID.randomUUID();
        QuoteUpdateRequestDTO dto = new QuoteUpdateRequestDTO();
        dto.setVendorCost(100);
        Phase phase = new Phase();
        when(phaseRepository.findById(eq(phaseId))).thenReturn(Optional.of(phase));
        when(phaseRepository.save(any(Phase.class))).thenReturn(phase);
        ResponseEntity<?> response = vendorService.submitQuote(phaseId, dto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Object body = response.getBody();
        assertNotNull(body);
        if (body instanceof AdminToastDTO dtoBody) {
            assertEquals("SUCCESS", dtoBody.getMessage());
        } else {
            assertTrue(body.toString().contains("SUCCESS"));
        }
    }
    @Test
    void testSubmitQuote_phaseNotFound() {
        UUID phaseId = UUID.randomUUID();
        QuoteUpdateRequestDTO dto = new QuoteUpdateRequestDTO();
        when(phaseRepository.findById(eq(phaseId))).thenReturn(Optional.empty());
        ResponseEntity<?> response = vendorService.submitQuote(phaseId, dto);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Object body = response.getBody();
        assertNotNull(body);
        if (body instanceof AdminToastDTO dtoBody) {
            assertEquals("ERROR", dtoBody.getMessage());
        } else {
            assertTrue(body.toString().contains("ERROR"));
        }
    }
    @Test
    void testSubmitQuote_exception() {
        UUID phaseId = UUID.randomUUID();
        QuoteUpdateRequestDTO dto = new QuoteUpdateRequestDTO();
        when(phaseRepository.findById(eq(phaseId))).thenThrow(new RuntimeException());
        ResponseEntity<?> response = vendorService.submitQuote(phaseId, dto);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Object body = response.getBody();
        assertNotNull(body);
        if (body instanceof AdminToastDTO dtoBody) {
            assertEquals("ERROR", dtoBody.getMessage());
        } else {
            assertTrue(body.toString().contains("ERROR"));
        }
    }

    // Helper to mock SecurityContext
    private void mockSecurityContext(User user) {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
}
