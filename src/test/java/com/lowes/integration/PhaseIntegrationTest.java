package com.lowes.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowes.Application;
import com.lowes.config.TestConfig;
import com.lowes.dto.request.PhaseRequestDTO;
import com.lowes.entity.Phase;
import com.lowes.entity.Room;
import com.lowes.entity.User;
import com.lowes.entity.Vendor;
import com.lowes.entity.enums.PhaseStatus;
import com.lowes.entity.enums.PhaseType;
import com.lowes.entity.enums.Role;
import com.lowes.repository.PhaseRepository;
import com.lowes.repository.RoomRepository;
import com.lowes.repository.VendorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.UUID;
import static org.hamcrest.Matchers.is;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
@Transactional
@Import(TestConfig.class)
class PhaseIntegrationTest {

    @Autowired private WebApplicationContext context;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private RoomRepository roomRepository;
    @Autowired private VendorRepository vendorRepository;
    @Autowired private PhaseRepository phaseRepository;

    private MockMvc mockMvc;
    private Room testRoom;
    private Vendor testVendor;

    @BeforeEach
    void setup() {
        mockMvc = webAppContextSetup(context).build();

        testRoom = new Room();
        testRoom.setExposedId(UUID.randomUUID());
        testRoom.setName("Test Room");
        roomRepository.saveAndFlush(testRoom);

        testVendor = new Vendor();
        testVendor.setExposedId(UUID.randomUUID());
        testVendor.setCompanyName("Test Vendor Co");
        testVendor.setAvailable(true);
        testVendor.setApproved(true);
        vendorRepository.saveAndFlush(testVendor);
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void testCreateAndGetPhaseById() throws Exception {
        User user = new User();
        user.setName("Vendor User");
        user.setEmail("vendor@example.com");
        user.setPassword("password");
        user.setRole(Role.VENDOR); // or whatever your enum or string role is

        Vendor vendor = new Vendor();
        vendor.setApproved(true);
        vendor.setAvailable(true);
        vendor.setCompanyName("Vendor Co");
        vendor.setUser(user); // ✅ Important!
        vendor = vendorRepository.saveAndFlush(vendor);


        PhaseRequestDTO request = new PhaseRequestDTO();
        request.setRoomId(testRoom.getExposedId());
        request.setVendorId(vendor.getExposedId());
        request.setPhaseName("Demo Phase");
        request.setPhaseType(PhaseType.CIVIL);
        request.setPhaseStatus(PhaseStatus.NOTSTARTED);
        request.setDescription("Demo Desc");
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusDays(3));

        mockMvc.perform(post("/phase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Phase saved = phaseRepository.findAll().get(0);

        mockMvc.perform(get("/phase/" + saved.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phaseName", is("Demo Phase")));
    }
    @Test
    @WithMockUser(roles = "CUSTOMER")
    void testSetVendorCost() throws Exception {
        // Save vendor (exposedId will be set by @PrePersist)
        User user = new User();
        user.setName("Vendor User");
        user.setEmail("vendor@example.com");
        user.setPassword("password");
        user.setRole(Role.VENDOR); // or whatever your enum or string role is

        Vendor vendor = new Vendor();
        vendor.setApproved(true);
        vendor.setAvailable(true);
        vendor.setCompanyName("Vendor Co");
        vendor.setUser(user); // ✅ Important!
        vendor = vendorRepository.saveAndFlush(vendor);

        // Assert exposedId is not null (critical!)
        assertThat(vendor.getExposedId()).isNotNull();

        // Save room
        Room room = new Room();
        room.setExposedId(UUID.randomUUID());
        room.setName("Cost Room");
        room = roomRepository.saveAndFlush(room);

        // Save phase with vendor assigned
        Phase phase = new Phase();
        phase.setRoom(room);
        phase.setVendor(vendor); // ✅ assign managed vendor
        phase.setPhaseType(PhaseType.CIVIL);
        phase.setPhaseName("Vendor Cost Phase");
        phase.setPhaseStatus(PhaseStatus.NOTSTARTED);
        phase.setStartDate(LocalDate.now());
        phase.setEndDate(LocalDate.now().plusDays(5));
        phase = phaseRepository.saveAndFlush(phase);

        // ✅ Sanity check: vendor must be assigned in DB
        assertThat(phase.getVendor()).isNotNull();
        assertThat(phase.getVendor().getExposedId()).isEqualTo(vendor.getExposedId());

        // Call API
        mockMvc.perform(post("/phase/vendor/{vendorId}/phase/{phaseId}/cost",
                        vendor.getExposedId(), phase.getId())
                        .param("cost", "500"))
                .andDo(print()) // helps if it still fails
                .andExpect(status().isOk())
                .andExpect(content().string("Cost updated successfully"));
    }




    @Test
    @WithMockUser(roles = "CUSTOMER")
    void testUpdatePhase() throws Exception {
        Phase phase = new Phase();
        phase.setRoom(testRoom);
        phase.setVendor(testVendor);
        phase.setPhaseName("Old Name");
        phase.setPhaseType(PhaseType.TILING);
        phase.setPhaseStatus(PhaseStatus.NOTSTARTED);
        phase.setStartDate(LocalDate.now());
        phase.setEndDate(LocalDate.now().plusDays(2));
        phase = phaseRepository.saveAndFlush(phase);

        PhaseRequestDTO update = new PhaseRequestDTO();
        update.setPhaseName("Updated Name");
        update.setPhaseStatus(PhaseStatus.COMPLETED);
        update.setPhaseType(PhaseType.TILING);
        update.setStartDate(LocalDate.now());
        update.setEndDate(LocalDate.now().plusDays(4));

        mockMvc.perform(put("/phase/" + phase.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phaseName", is("Updated Name")));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void testGetMaterialsByPhaseId_ShouldReturnEmptyList() throws Exception {
        Phase phase = new Phase();
        phase.setRoom(testRoom);
        phase.setVendor(testVendor);
        phase.setPhaseName("Materialless Phase");
        phase.setPhaseType(PhaseType.ELECTRICAL);
        phase.setPhaseStatus(PhaseStatus.NOTSTARTED);
        phase.setStartDate(LocalDate.now());
        phase.setEndDate(LocalDate.now().plusDays(5));
        phase = phaseRepository.saveAndFlush(phase);

        mockMvc.perform(get("/phase/materials")
                        .param("id", phase.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void testGetPhasesByRoom() throws Exception {
        mockMvc.perform(get("/phase/room/" + testRoom.getExposedId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void testDoesPhaseExist() throws Exception {
        mockMvc.perform(get("/phase/phase/exists")
                        .param("roomId", testRoom.getExposedId().toString())
                        .param("phaseType", "ELECTRICAL"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void testGetPhasesByRenovationType() throws Exception {
        mockMvc.perform(get("/phase/phases/by-renovation-type/KITCHEN_RENOVATION"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void testCalculateTotalCost() throws Exception {
        Phase phase = new Phase();
        phase.setRoom(testRoom);
        phase.setVendor(testVendor);
        phase.setPhaseType(PhaseType.ELECTRICAL);
        phase.setPhaseStatus(PhaseStatus.NOTSTARTED);
        phase.setPhaseName("Cost Test Phase");
        phase.setStartDate(LocalDate.now());
        phase.setEndDate(LocalDate.now().plusDays(7));
        phase.setVendorCost(200);
        phase = phaseRepository.saveAndFlush(phase);

        mockMvc.perform(get("/phase/" + phase.getId() + "/total-cost"))
                .andExpect(status().isOk())
                .andExpect(content().string("200"));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void testDeletePhase() throws Exception {
        Phase phase = new Phase();
        phase.setRoom(testRoom);
        phase.setVendor(testVendor);
        phase.setPhaseStatus(PhaseStatus.NOTSTARTED);
        phase.setPhaseType(PhaseType.CIVIL);
        phase.setPhaseName("Delete Me");
        phase.setStartDate(LocalDate.now());
        phase.setEndDate(LocalDate.now().plusDays(3));
        phase = phaseRepository.saveAndFlush(phase);

        UUID phaseId = phase.getId();

        mockMvc.perform(delete("/phase/delete/" + phaseId))
                .andExpect(status().isOk());

        assertThat(phaseRepository.findById(phaseId)).isEmpty();
    }
}
