package com.lowes.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowes.Application;
import com.lowes.config.TestConfig;
import com.lowes.dto.request.PhaseRequestDTO;
import com.lowes.entity.Room;
import com.lowes.entity.Vendor;
import com.lowes.entity.enums.PhaseStatus;
import com.lowes.entity.enums.PhaseType;
import com.lowes.repository.PhaseRepository;
import com.lowes.repository.RoomRepository;
import com.lowes.repository.VendorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
@Transactional
class PhaseIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private PhaseRepository phaseRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private UUID roomExposedId;
    private UUID vendorExposedId;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        Room room = roomRepository.findAll().get(0);
        Vendor vendor = vendorRepository.findAll().get(0);
        roomExposedId = room.getExposedId();
        vendorExposedId = vendor.getExposedId();
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void testCreatePhase() throws Exception {
        PhaseRequestDTO request = new PhaseRequestDTO();
        request.setRoomId(roomExposedId);
        request.setVendorId(vendorExposedId);
        request.setPhaseName("Painting Phase");
        request.setPhaseType(PhaseType.PAINTING);
        request.setPhaseStatus(PhaseStatus.NOTSTARTED);
        request.setDescription("Wall painting");
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusDays(7));

        mockMvc.perform(post("/phase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Phase created successfully"));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void testGetPhasesByRoom() throws Exception {
        mockMvc.perform(get("/phase/room/" + roomExposedId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void testDoesPhaseExist() throws Exception {
        mockMvc.perform(get("/phase/phase/exists")
                        .param("roomId", roomExposedId.toString())
                        .param("phaseType", "ELECTRICAL"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void testGetPhasesByRenovationType() throws Exception {
        mockMvc.perform(get("/phase/phases/by-renovation-type/KITCHEN_RENOVATION"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void testCalculateTotalCost() throws Exception {
        UUID phaseId = phaseRepository.findAll().get(0).getId();

        mockMvc.perform(get("/phase/" + phaseId + "/total-cost"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void testDeletePhase() throws Exception {
        UUID phaseId = phaseRepository.findAll().get(0).getId();

        mockMvc.perform(delete("/phase/delete/" + phaseId))
                .andDo(print())
                .andExpect(status().isOk());

        assertThat(phaseRepository.findById(phaseId)).isEmpty();
    }
}
