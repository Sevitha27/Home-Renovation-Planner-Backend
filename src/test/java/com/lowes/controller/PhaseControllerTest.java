package com.lowes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowes.dto.request.PhaseRequestDTO;
import com.lowes.dto.response.PhaseMaterialUserResponse;
import com.lowes.dto.response.PhaseResponse;
import com.lowes.dto.response.PhaseResponseDTO;
import com.lowes.entity.Phase;
import com.lowes.entity.enums.PhaseStatus;
import com.lowes.entity.enums.PhaseType;
import com.lowes.entity.enums.RenovationType;
import com.lowes.repository.PhaseRepository;
import com.lowes.service.PhaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PhaseControllerTest {

    @InjectMocks
    private PhaseController phaseController;

    @Mock
    private PhaseService phaseService;

    @Mock
    private PhaseRepository phaseRepository;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private UUID phaseId;
    private UUID roomId;
    private UUID vendorId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(phaseController).build();
        objectMapper = new ObjectMapper().findAndRegisterModules();

        phaseId = UUID.randomUUID();
        roomId = UUID.randomUUID();
        vendorId = UUID.randomUUID();
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void createPhase_shouldReturnSuccessMessage() throws Exception {
        PhaseRequestDTO dto = new PhaseRequestDTO();
        dto.setPhaseName("New Phase");
        dto.setPhaseType(PhaseType.CIVIL);
        dto.setPhaseStatus(PhaseStatus.NOTSTARTED);
        dto.setRoomId(roomId);
        dto.setVendorId(vendorId);
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusDays(2));

        doNothing().when(phaseService).createPhase(any());

        mockMvc.perform(post("/phase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Phase created successfully")));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void createPhase_shouldReturnExceptionMessage() throws Exception {
        PhaseRequestDTO dto = new PhaseRequestDTO();
        dto.setPhaseName("Fail");
        dto.setPhaseType(PhaseType.CIVIL);
        dto.setPhaseStatus(PhaseStatus.NOTSTARTED);
        dto.setRoomId(roomId);
        dto.setVendorId(vendorId);
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusDays(2));

        doThrow(new RuntimeException("DB error")).when(phaseService).createPhase(any());

        mockMvc.perform(post("/phase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("exception occured")));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getPhaseById_shouldReturnPhase() throws Exception {
        PhaseResponse response = new PhaseResponse();
        response.setId(phaseId);
        response.setPhaseName("Civil");

        when(phaseService.getPhaseById(phaseId)).thenReturn(response);

        mockMvc.perform(get("/phase/{id}", phaseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phaseName", is("Civil")));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void setVendorCost_shouldReturnSuccessMessage() throws Exception {
        doReturn(new PhaseResponse()).when(phaseService)
                .setVendorCostForPhase(vendorId, phaseId, 100);


        mockMvc.perform(post("/phase/vendor/{vendorId}/phase/{phaseId}/cost", vendorId, phaseId)
                        .param("cost", "100"))
                .andExpect(status().isOk())
                .andExpect(content().string("Cost updated successfully"));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void updatePhase_shouldReturnUpdatedPhase() throws Exception {
        Phase phase = new Phase();
        phase.setPhaseName("Updated");

        when(phaseService.updatePhase(eq(phaseId), any())).thenReturn(phase);

        PhaseRequestDTO update = new PhaseRequestDTO();
        update.setPhaseName("Updated");
        update.setPhaseType(PhaseType.ELECTRICAL);
        update.setPhaseStatus(PhaseStatus.INPROGRESS);
        update.setRoomId(roomId);
        update.setVendorId(vendorId);
        update.setStartDate(LocalDate.now());
        update.setEndDate(LocalDate.now().plusDays(3));

        mockMvc.perform(put("/phase/{id}", phaseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phaseName", is("Updated")));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getPhasesByRoom_shouldReturnList() throws Exception {
        PhaseResponse response = new PhaseResponse();
        response.setPhaseName("Room Phase");

        when(phaseService.getPhasesByRoomExposedId(roomId)).thenReturn(List.of(response));

        mockMvc.perform(get("/phase/room/{roomExposedId}", roomId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].phaseName", is("Room Phase")));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void calculatePhaseTotalCost_shouldReturnAmount() throws Exception {
        when(phaseService.calculateTotalCost(phaseId)).thenReturn(1500);

        mockMvc.perform(get("/phase/{id}/total-cost", phaseId))
                .andExpect(status().isOk())
                .andExpect(content().string("1500"));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getMaterialsByPhaseId_shouldReturnList() throws Exception {
        PhaseMaterialUserResponse m = new PhaseMaterialUserResponse();
        m.setName("Cement");

        when(phaseService.getAllPhaseMaterialsByPhaseId(phaseId)).thenReturn(List.of(m));

        mockMvc.perform(get("/phase/materials")
                        .param("id", phaseId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("Cement")));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getPhasesByRenovationType_shouldReturnList() throws Exception {
        when(phaseService.getPhasesByRenovationType(RenovationType.BEDROOM_RENOVATION))
                .thenReturn(List.of(PhaseType.CIVIL, PhaseType.TILING));

        mockMvc.perform(get("/phase/phases/by-renovation-type/BEDROOM_RENOVATION"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]", is("CIVIL")))
                .andExpect(jsonPath("$[1]", is("TILING")));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getPhasesByRenovationType_shouldReturn404IfNull() throws Exception {
        when(phaseService.getPhasesByRenovationType(RenovationType.EXTERIOR_RENOVATION)).thenReturn(null);

        mockMvc.perform(get("/phase/phases/by-renovation-type/EXTERIOR_RENOVATION"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void doesPhaseExist_shouldReturnTrueOrFalse() throws Exception {
        when(phaseRepository.existsByRoomExposedIdAndPhaseType(roomId, PhaseType.CIVIL)).thenReturn(true);

        mockMvc.perform(get("/phase/phase/exists")
                        .param("roomId", roomId.toString())
                        .param("phaseType", "CIVIL"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void deletePhase_shouldSucceed() throws Exception {
        doNothing().when(phaseService).deletePhase(phaseId);

        mockMvc.perform(delete("/phase/delete/{id}", phaseId))
                .andExpect(status().isOk());

        verify(phaseService, times(1)).deletePhase(phaseId);
    }
}
