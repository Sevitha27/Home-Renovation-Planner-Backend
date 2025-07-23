package com.lowes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowes.dto.response.BudgetOverviewResponseDTO;
import com.lowes.dto.response.PhaseCostDTO;
import com.lowes.dto.response.RoomCostDTO;
import com.lowes.entity.User;
import com.lowes.service.BudgetOverviewService;
import com.lowes.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BudgetOverviewControllerTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private BudgetOverviewService budgetOverviewService;

    @InjectMocks
    private BudgetOverviewController budgetOverviewController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(budgetOverviewController).build();
        objectMapper = new ObjectMapper();

        // Mock authenticated user
        mockUser = new User();
        mockUser.setExposedId(UUID.randomUUID());
        mockUser.setEmail("testuser@example.com");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(mockUser, null, Collections.emptyList())
        );
    }

    @Test
    void testGetBudgetOverview_Success() throws Exception {
        UUID projectId = UUID.randomUUID();

        List<RoomCostDTO> rooms = Arrays.asList(
                new RoomCostDTO(UUID.randomUUID(), "Kitchen", 15000),
                new RoomCostDTO(UUID.randomUUID(), "Living Room", 17000)
        );

        List<PhaseCostDTO> phases = Arrays.asList(
                new PhaseCostDTO(UUID.randomUUID(), "Wiring Phase", "ELECTRICAL", 12000, 3000, 15000),
                new PhaseCostDTO(UUID.randomUUID(), "Plumbing Phase", "PLUMBING", 13000, 4000, 17000)
        );

        BudgetOverviewResponseDTO responseDTO = new BudgetOverviewResponseDTO(
                projectId,
                "Home Renovation",
                100000,
                32000,
                rooms,
                phases
        );

        doReturn(responseDTO).when(budgetOverviewService).getBudgetOverview(any(UUID.class), any(UUID.class));

        mockMvc.perform(get("/api/projects/" + projectId + "/budget-overview")
                        .header("Authorization", "Bearer mockToken")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.projectName").value("Home Renovation"))
                .andExpect(jsonPath("$.estimatedBudget").value(100000))
                .andExpect(jsonPath("$.totalActualCost").value(32000))
                .andExpect(jsonPath("$.rooms.length()").value(2))
                .andExpect(jsonPath("$.phases.length()").value(2));
    }


}
