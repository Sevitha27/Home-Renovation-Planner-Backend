package com.lowes.controller;

import com.lowes.dto.request.vendor.QuoteUpdateRequestDTO;
import com.lowes.service.VendorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class VendorControllerTest {

    @Mock
    private VendorService vendorService;

    @InjectMocks
    private VendorController vendorController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(vendorController).build();
    }

    @Test
    void testGetAssignedPhases() throws Exception {
        doReturn(ResponseEntity.ok("phases-list")).when(vendorService).getAssignedPhases();
        mockMvc.perform(get("/vendor/phases"))
                .andExpect(status().isOk())
                .andExpect(content().string("phases-list"));
    }

    @Test
    void testSubmitQuote() throws Exception {
        UUID phaseId = UUID.randomUUID();
        QuoteUpdateRequestDTO dto = new QuoteUpdateRequestDTO();
        String json = "{}";

        doReturn(ResponseEntity.ok(Map.of("message", "SUCCESS")))
            .when(vendorService).submitQuote(eq(phaseId), any(QuoteUpdateRequestDTO.class));

        mockMvc.perform(post("/vendor/phase/" + phaseId + "/quote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("SUCCESS"));
    }

    @Test
    void testGetVendorApprovalStatus() throws Exception {
        doReturn(ResponseEntity.ok(Map.of("approval", true)))
            .when(vendorService).getVendorApprovalStatus();
        mockMvc.perform(get("/vendor/getVendorDetails"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.approval").value(true));
    }
}
