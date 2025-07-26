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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
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
    private Phase phase;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(phaseController).build();
        objectMapper = new ObjectMapper()
                .findAndRegisterModules();

        phaseId = UUID.randomUUID();
        roomId = UUID.randomUUID();

        phase = new Phase();
        phase.setId(phaseId);
        phase.setPhaseName("Civil Work");
        phase.setPhaseStatus(PhaseStatus.NOTSTARTED);
        phase.setPhaseType(PhaseType.CIVIL);
    }

    @Test
    void createPhase_shouldReturnSuccessMessage() throws Exception {
        PhaseRequestDTO request = new PhaseRequestDTO();
        request.setPhaseName("New Phase");
        request.setPhaseStatus(PhaseStatus.NOTSTARTED);
        request.setPhaseType(PhaseType.CIVIL);
        request.setRoomId(UUID.randomUUID());
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusDays(5));

        doNothing().when(phaseService).createPhase(any());

        mockMvc.perform(post("/phase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Phase created successfully")));
    }

    @Test
    void getPhaseById_shouldReturnPhase() throws Exception {
        PhaseResponse response = new PhaseResponse();
        response.setId(phaseId);
        response.setPhaseName("Civil Work");

        when(phaseService.getPhaseById(phaseId)).thenReturn(response);

        mockMvc.perform(get("/phase/{id}", phaseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phaseName", is("Civil Work")));
    }




    @Test
    void updatePhase_shouldReturnUpdatedPhase() throws Exception {
        PhaseRequestDTO update = new PhaseRequestDTO();
        update.setPhaseName("Updated");
        update.setPhaseStatus(PhaseStatus.INPROGRESS);
        update.setPhaseType(PhaseType.CIVIL);
        update.setStartDate(LocalDate.now());
        update.setEndDate(LocalDate.now().plusDays(2));
        update.setRoomId(UUID.randomUUID());
        update.setVendorId(UUID.randomUUID());


        when(phaseService.updatePhase(eq(phaseId), any())).thenReturn(phase);

        mockMvc.perform(put("/phase/{id}", phaseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phaseName", is("Civil Work")));
    }

    @Test
    void getPhasesByRoomExposedId_shouldReturnList() throws Exception {
        UUID roomId = UUID.randomUUID();
        PhaseResponse responseDTO = new PhaseResponse();
        responseDTO.setPhaseName("Demo Phase");

        when(phaseService.getPhasesByRoomExposedId(roomId)).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/phase/room/{roomExposedId}", roomId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].phaseName", is("Demo Phase")));

        verify(phaseService, times(1)).getPhasesByRoomExposedId(roomId);
    }

    @Test
    void calculatePhaseTotalCost_shouldReturnInteger() throws Exception {
        when(phaseService.calculateTotalCost(phaseId)).thenReturn(1000);

        mockMvc.perform(get("/phase/{id}/total-cost", phaseId))
                .andExpect(status().isOk())
                .andExpect(content().string("1000"));
    }

    @Test
    void getMaterialsByPhaseId_shouldReturnMaterialList() throws Exception {
        PhaseMaterialUserResponse resp = new PhaseMaterialUserResponse();
        resp.setName("Steel");

        when(phaseService.getAllPhaseMaterialsByPhaseId(phaseId)).thenReturn(List.of(resp));

        mockMvc.perform(get("/phase/materials").param("id", phaseId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("Steel")));
    }

    @Test
    void getPhasesByRenovationType_shouldReturnPhaseTypes() throws Exception {
        when(phaseService.getPhasesByRenovationType(RenovationType.KITCHEN_RENOVATION))
                .thenReturn(List.of(PhaseType.CIVIL, PhaseType.PAINTING));

        mockMvc.perform(get("/phase/phases/by-renovation-type/KITCHEN_RENOVATION"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]", is("CIVIL")))
                .andExpect(jsonPath("$[1]", is("PAINTING")));
    }

    @Test
    void doesPhaseExist_shouldReturnTrue() throws Exception {
        when(phaseRepository.existsByRoomExposedIdAndPhaseType(roomId, PhaseType.CIVIL)).thenReturn(true);

        mockMvc.perform(get("/phase/phase/exists")
                        .param("roomId", roomId.toString())
                        .param("phaseType", "CIVIL"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void deletePhase_shouldCallService() throws Exception {
        doNothing().when(phaseService).deletePhase(phaseId);

        mockMvc.perform(delete("/phase/delete/{id}", phaseId))
                .andExpect(status().isOk());

        verify(phaseService).deletePhase(phaseId);
    }
}
