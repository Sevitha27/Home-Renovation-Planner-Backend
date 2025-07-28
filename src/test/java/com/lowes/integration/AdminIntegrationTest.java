package com.lowes.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowes.Application;
import com.lowes.config.TestConfig;
import com.lowes.dto.request.admin.MaterialRequestAdminDTO;
import com.lowes.entity.Material;
import com.lowes.entity.User;
import com.lowes.entity.Vendor;
import com.lowes.entity.enums.PhaseType;
import com.lowes.entity.enums.Role;
import com.lowes.entity.enums.Unit;
import com.lowes.repository.MaterialRepository;
import com.lowes.repository.UserRepository;
import com.lowes.repository.VendorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
@Transactional
@Import(TestConfig.class)
class AdminIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllUsers() throws Exception {
        // Create test users
        User user1 = createTestUser("user1@test.com", "User1", Role.CUSTOMER);
        User user2 = createTestUser("user2@test.com", "User2", Role.CUSTOMER); // Only customers are returned
        userRepository.saveAll(List.of(user1, user2));

        mockMvc.perform(get("/admin/users")
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllVendors() throws Exception {
        // Create test vendors
        Vendor vendor1 = createTestVendor("vendor1@test.com", "Vendor1");
        Vendor vendor2 = createTestVendor("vendor2@test.com", "Vendor2");

        // Save users first, then vendors
        userRepository.save(vendor1.getUser());
        userRepository.save(vendor2.getUser());
        vendorRepository.saveAll(List.of(vendor1, vendor2));

        mockMvc.perform(get("/admin/vendors/approved")
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllMaterials() throws Exception {
        // Create test materials
        Material material1 = createTestMaterial("Test Material 1", "Description 1", BigDecimal.valueOf(10), Unit.UNITS);
        Material material2 = createTestMaterial("Test Material 2", "Description 2", BigDecimal.valueOf(25), Unit.KG);
        materialRepository.saveAll(List.of(material1, material2));

        mockMvc.perform(get("/admin/materials")
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAddMaterial() throws Exception {
        MaterialRequestAdminDTO request = new MaterialRequestAdminDTO();
        request.setName("New Test Material");
        request.setPricePerQuantity(15);
        request.setUnit(Unit.UNITS);
        request.setPhaseType(PhaseType.ELECTRICAL);

        mockMvc.perform(post("/admin/materials")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("SUCCESS"));

        // Verify material was saved by checking if any material with this name exists
        assertThat(materialRepository.findAll().stream()
                .anyMatch(m -> m.getName().equals("New Test Material"))).isTrue();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateMaterial() throws Exception {
        // Create a material first
        Material material = createTestMaterial("Original Name", "Original Description", BigDecimal.valueOf(10.00), Unit.UNITS);
        material = materialRepository.save(material);

        MaterialRequestAdminDTO request = new MaterialRequestAdminDTO();
        request.setName("Updated Name");
        request.setPricePerQuantity(20);
        request.setUnit(Unit.KG);
        request.setPhaseType(PhaseType.PLUMBING);

        mockMvc.perform(put("/admin/materials/{id}", material.getExposedId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("SUCCESS"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteMaterial() throws Exception {
        // Create a material first
        Material material = createTestMaterial("To Delete", "Will be deleted", BigDecimal.valueOf(5), Unit.UNITS);
        material = materialRepository.save(material);

        mockMvc.perform(patch("/admin/materials/delete/{id}", material.getExposedId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("SUCCESS"));

        // Verify material was soft deleted (deleted flag set to true)
        Optional<Material> deletedMaterial = materialRepository.findByExposedId(material.getExposedId());
        assertThat(deletedMaterial).isPresent();
        assertThat(deletedMaterial.get().isDeleted()).isTrue();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetMaterialById() throws Exception {
        // Create a material first
        Material material = createTestMaterial("Test Material", "Test Description", BigDecimal.valueOf(12), Unit.UNITS);
        material = materialRepository.save(material);

        mockMvc.perform(get("/admin/materials/{id}", material.getExposedId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Test Material"))
                .andExpect(jsonPath("$.pricePerQuantity").value(12));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetMaterialByIdNotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        mockMvc.perform(get("/admin/materials/{id}", nonExistentId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateMaterialNotFound() throws Exception {
        MaterialRequestAdminDTO request = new MaterialRequestAdminDTO();
        request.setName("Updated Name");
        request.setPricePerQuantity(20);
        request.setUnit(Unit.KG);
        request.setPhaseType(PhaseType.PLUMBING);

        UUID nonExistentId = UUID.randomUUID();
        mockMvc.perform(put("/admin/materials/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteMaterialNotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        mockMvc.perform(patch("/admin/materials/delete/{id}", nonExistentId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // Helper methods to create test data
    private User createTestUser(String email, String name, Role role) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setPassword(passwordEncoder.encode("password123"));
        user.setRole(role);
        user.setContact("1234567890");
        return user;
    }

    private Vendor createTestVendor(String email, String name) {
        // Create the User entity first
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setPassword(passwordEncoder.encode("password123"));
        user.setRole(Role.VENDOR);
        user.setContact("1234567890");

        // Create the Vendor entity
        Vendor vendor = new Vendor();
        vendor.setCompanyName(name + " Company");
        vendor.setExperience("5 years");
        vendor.setAvailable(true);
        vendor.setApproved(true);
        vendor.setUser(user);
        vendor.setSkills(new ArrayList<>()); // Initialize empty skills list

        return vendor;
    }

    private Material createTestMaterial(String name, String description, BigDecimal price, Unit unit) {
        Material material = new Material();
        material.setName(name);
        material.setExposedId(UUID.randomUUID());
        material.setPricePerQuantity(price.intValue());
        material.setUnit(unit);
        material.setPhaseType(PhaseType.ELECTRICAL);
        material.setDeleted(false);
        return material;
    }
}