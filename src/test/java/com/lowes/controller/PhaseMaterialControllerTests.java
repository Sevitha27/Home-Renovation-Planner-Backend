package com.lowes.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowes.dto.request.PhaseMaterialUserRequest;
import com.lowes.dto.response.MaterialUserResponse;
import com.lowes.dto.response.PhaseMaterialUserResponse;
import com.lowes.dto.response.PhaseResponse;
import com.lowes.entity.enums.PhaseStatus;
import com.lowes.entity.enums.PhaseType;
import com.lowes.entity.enums.Unit;
import com.lowes.exception.ElementNotFoundException;
import com.lowes.exception.EmptyException;
import com.lowes.exception.OperationNotAllowedException;
import com.lowes.service.PhaseMaterialService;
import com.lowes.service.PhaseService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PhaseMaterialControllerTests {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PhaseMaterialService phaseMaterialService;

    @Autowired
    ObjectMapper objectMapper;

    @TestConfiguration
    static class DisableJWTFilterForTest {
        @Bean
        @Primary
        public com.lowes.config.JWTAuthenticationFilter jwtAuthenticationFilter() {
            return new com.lowes.config.JWTAuthenticationFilter(null, null) {
                @Override
                protected void doFilterInternal(
                        jakarta.servlet.http.HttpServletRequest request,
                        jakarta.servlet.http.HttpServletResponse response,
                        jakarta.servlet.FilterChain filterChain
                ) throws java.io.IOException, jakarta.servlet.ServletException {
                    filterChain.doFilter(request, response);
                }
            };
        }
    }

    private PhaseMaterialUserRequest getPhaseMaterialUserRequest(){

        PhaseMaterialUserRequest phaseMaterialUserRequest = PhaseMaterialUserRequest.builder()
                .materialExposedId(UUID.fromString("4f12010a-f273-46b5-b9f7-31df7a747944"))
                .quantity(100)
                .build();

        return phaseMaterialUserRequest;
    }

    private PhaseMaterialUserResponse getPhaseMaterialUserResponse(){

        MaterialUserResponse materialUserResponse = MaterialUserResponse.builder()
                .exposedId(UUID.fromString("4f12010a-f273-46b5-b9f7-31df7a747944"))
                .name("Cement")
                .unit(Unit.KG)
                .phaseType(PhaseType.CIVIL)
                .pricePerQuantity(400)
                .build();

        PhaseResponse phaseResponse = PhaseResponse.builder()
                .id(UUID.fromString("8e8541b7-7120-4001-98ef-36746489c809"))
                .phaseName("Foundation Phase")
                .description("Initial groundwork")
                .startDate(LocalDate.of(2025, 7, 1))
                .endDate(LocalDate.of(2025, 7, 20))
                .phaseType(PhaseType.CIVIL)
                .phaseStatus(PhaseStatus.NOTSTARTED)
                .build();

        PhaseMaterialUserResponse phaseMaterialUserResponse = PhaseMaterialUserResponse.builder()
                .exposedId(UUID.fromString("6fc3b98b-d6f1-4828-8aae-1df2dc0a061c"))
                .name("Cement")
                .unit(Unit.KG)
                .pricePerQuantity(400)
                .phaseType(PhaseType.CIVIL)
                .quantity(25)
                .totalPrice(25 * 400)
                .materialUserResponse(materialUserResponse)
                .phaseResponse(phaseResponse)
                .build();

        return phaseMaterialUserResponse;
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"CUSTOMER"})
    public void addPhaseMaterialsToPhaseByPhaseId() throws Exception {

        String phaseMaterialUserRequestString = objectMapper.writeValueAsString(List.of(getPhaseMaterialUserRequest()));

        PhaseMaterialUserResponse phaseMaterialUserResponse = getPhaseMaterialUserResponse();

        UUID phaseId = UUID.fromString("8e8541b7-7120-4001-98ef-36746489c809");

        Mockito.when(phaseMaterialService.addPhaseMaterialsToPhaseByPhaseId(eq(phaseId), Mockito.<List<PhaseMaterialUserRequest>>any())).thenReturn(List.of(phaseMaterialUserResponse));

        mockMvc.perform(post("/api/user/phase/{phase-id}/phase-materials",phaseId)
                .content(phaseMaterialUserRequestString)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Cement"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"CUSTOMER"})
    public void updatePhaseMaterialQuantityByExposedId() throws Exception {

        PhaseMaterialUserResponse phaseMaterialUserResponse = getPhaseMaterialUserResponse();

        UUID phaseMaterialExposedId = UUID.fromString("8030b570-9e9f-482f-84b4-e8323c94d2e0");
        Mockito.when(phaseMaterialService.updatePhaseMaterialQuantityByExposedId(eq(phaseMaterialExposedId),anyInt())).thenReturn(phaseMaterialUserResponse);

        mockMvc.perform(patch("/api/user/phase-materials/{phase-material-id}",phaseMaterialExposedId)
                .param("quantity",String.valueOf(25)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Cement"))
                .andExpect(jsonPath("$.quantity").value(25));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"CUSTOMER"})
    public void deletePhaseMaterialByExposedId() throws Exception {

        PhaseMaterialUserResponse phaseMaterialUserResponse = getPhaseMaterialUserResponse();

        UUID phaseMaterialExposedId = UUID.fromString("8030b570-9e9f-482f-84b4-e8323c94d2e0");
        Mockito.when(phaseMaterialService.deletePhaseMaterialByExposedId(eq(phaseMaterialExposedId))).thenReturn(phaseMaterialUserResponse);

        mockMvc.perform(delete("/api/user/phase-materials/{phase-material-id}",phaseMaterialExposedId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Cement"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"CUSTOMER"})
    public void addPhaseMaterials_ElementNotFoundException_Returns404() throws Exception {
        UUID phaseId = UUID.randomUUID();
        List<PhaseMaterialUserRequest> requestList = List.of(getPhaseMaterialUserRequest());

        Mockito.when(phaseMaterialService.addPhaseMaterialsToPhaseByPhaseId(eq(phaseId), any()))
                .thenThrow(new ElementNotFoundException("Phase Not Found"));

        mockMvc.perform(post("/api/user/phase/{phase-id}/phase-materials", phaseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestList)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("Phase Not Found"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"CUSTOMER"})
    public void addPhaseMaterials_EmptyException_Returns400() throws Exception {
        UUID phaseId = UUID.randomUUID();

        Mockito.when(phaseMaterialService.addPhaseMaterialsToPhaseByPhaseId(eq(phaseId), any()))
                .thenThrow(new EmptyException("List is empty"));

        mockMvc.perform(post("/api/user/phase/{phase-id}/phase-materials", phaseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("List is empty"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"CUSTOMER"})
    public void addPhaseMaterials_OperationNotAllowedException_Returns400() throws Exception {
        UUID phaseId = UUID.randomUUID();
        List<PhaseMaterialUserRequest> requestList = List.of(getPhaseMaterialUserRequest());

        Mockito.when(phaseMaterialService.addPhaseMaterialsToPhaseByPhaseId(eq(phaseId), any()))
                .thenThrow(new OperationNotAllowedException("Phase type mismatch"));

        mockMvc.perform(post("/api/user/phase/{phase-id}/phase-materials", phaseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestList)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Phase type mismatch"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"CUSTOMER"})
    public void addPhaseMaterials_GenericException_Returns500() throws Exception {
        UUID phaseId = UUID.randomUUID();
        List<PhaseMaterialUserRequest> requestList = List.of(getPhaseMaterialUserRequest());

        Mockito.when(phaseMaterialService.addPhaseMaterialsToPhaseByPhaseId(eq(phaseId), any()))
                .thenThrow(new RuntimeException("Unexpected failure"));

        mockMvc.perform(post("/api/user/phase/{phase-id}/phase-materials", phaseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestList)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$").value("Internal Server Error : Unexpected failure"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"CUSTOMER"})
    public void updatePhaseMaterialQuantity_IllegalArgumentException_Returns400() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.when(phaseMaterialService.updatePhaseMaterialQuantityByExposedId(eq(id), anyInt()))
                .thenThrow(new IllegalArgumentException("Invalid quantity"));

        mockMvc.perform(patch("/api/user/phase-materials/{phase-material-id}", id)
                        .param("quantity", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Illegal Argument Exception : Invalid quantity"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"CUSTOMER"})
    public void updatePhaseMaterialQuantity_ElementNotFoundException_Returns404() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.when(phaseMaterialService.updatePhaseMaterialQuantityByExposedId(eq(id), anyInt()))
                .thenThrow(new ElementNotFoundException("Not found"));

        mockMvc.perform(patch("/api/user/phase-materials/{phase-material-id}", id)
                        .param("quantity", "10"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("Not found"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"CUSTOMER"})
    public void updatePhaseMaterialQuantity_GenericException_Returns500() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.when(phaseMaterialService.updatePhaseMaterialQuantityByExposedId(eq(id), anyInt()))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(patch("/api/user/phase-materials/{phase-material-id}", id)
                        .param("quantity", "10"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$").value("Internal Server Error : Unexpected error"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"CUSTOMER"})
    public void deletePhaseMaterial_ElementNotFoundException_Returns404() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.when(phaseMaterialService.deletePhaseMaterialByExposedId(eq(id)))
                .thenThrow(new ElementNotFoundException("Not found"));

        mockMvc.perform(delete("/api/user/phase-materials/{phase-material-id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("Not found"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"CUSTOMER"})
    public void deletePhaseMaterial_GenericException_Returns500() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.when(phaseMaterialService.deletePhaseMaterialByExposedId(eq(id)))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(delete("/api/user/phase-materials/{phase-material-id}", id))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$").value("Internal Server Error : Unexpected error"));
    }



}
