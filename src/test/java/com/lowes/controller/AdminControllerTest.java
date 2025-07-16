package com.lowes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowes.dto.request.admin.MaterialRequestAdminDTO;
import com.lowes.dto.response.admin.AdminPaginatedResponseDTO;
import com.lowes.dto.response.admin.AdminToastDTO;
import com.lowes.dto.response.admin.MaterialResponseAdminDTO;
import com.lowes.dto.response.admin.UserResponseAdminDTO;
import com.lowes.dto.response.admin.VendorResponseAdminDTO;
import com.lowes.entity.enums.PhaseType;
import com.lowes.entity.enums.Unit;
import com.lowes.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
        objectMapper = new ObjectMapper();
    }

    // Customer Tests
    @Test
    void testGetAllUsers_Success() throws Exception {
        // Given
        List<UserResponseAdminDTO> users = Arrays.asList(
            createUserResponseAdminDTO("user1@test.com", "User1"),
            createUserResponseAdminDTO("user2@test.com", "User2")
        );
        
        AdminPaginatedResponseDTO<UserResponseAdminDTO> response = AdminPaginatedResponseDTO.<UserResponseAdminDTO>builder()
            .content(users)
            .pageNumber(0)
            .pageSize(10)
            .totalElements(2L)
            .totalPages(1)
            .build();

        doReturn(ResponseEntity.ok().body(response)).when(adminService).getAllCustomers(0, 10);

        // When & Then
        mockMvc.perform(get("/admin/users")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content.length()").value(2))
            .andExpect(jsonPath("$.totalElements").value(2))
            .andExpect(jsonPath("$.pageNumber").value(0))
            .andExpect(jsonPath("$.pageSize").value(10));
    }

    @Test
    void testDeleteUser_Success() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();
        AdminToastDTO response = AdminToastDTO.builder().message("SUCCESS").build();
        doReturn(ResponseEntity.ok().body(response)).when(adminService).deleteUser(userId);

        // When & Then
        mockMvc.perform(delete("/admin/users/{id}", userId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value("SUCCESS"));
    }

    @Test
    void testDeleteUser_Error() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();
        AdminToastDTO response = AdminToastDTO.builder().message("ERROR").build();
        doReturn(ResponseEntity.badRequest().body(response)).when(adminService).deleteUser(userId);

        // When & Then
        mockMvc.perform(delete("/admin/users/{id}", userId))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value("ERROR"));
    }

    // Vendor Tests
    @Test
    void testGetApprovedVendors_Success() throws Exception {
        // Given
        List<VendorResponseAdminDTO> vendors = Arrays.asList(
            createVendorResponseAdminDTO("vendor1@test.com", "Vendor1"),
            createVendorResponseAdminDTO("vendor2@test.com", "Vendor2")
        );
        
        AdminPaginatedResponseDTO<VendorResponseAdminDTO> response = AdminPaginatedResponseDTO.<VendorResponseAdminDTO>builder()
            .content(vendors)
            .pageNumber(0)
            .pageSize(10)
            .totalElements(2L)
            .totalPages(1)
            .build();

        doReturn(ResponseEntity.ok().body(response)).when(adminService).getApprovedVendors(0, 10);

        // When & Then
        mockMvc.perform(get("/admin/vendors/approved")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content.length()").value(2))
            .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void testGetApprovalPendingVendors_Success() throws Exception {
        // Given
        List<VendorResponseAdminDTO> vendors = Arrays.asList(
            createVendorResponseAdminDTO("pending1@test.com", "Pending1"),
            createVendorResponseAdminDTO("pending2@test.com", "Pending2")
        );
        
        AdminPaginatedResponseDTO<VendorResponseAdminDTO> response = AdminPaginatedResponseDTO.<VendorResponseAdminDTO>builder()
            .content(vendors)
            .pageNumber(0)
            .pageSize(10)
            .totalElements(2L)
            .totalPages(1)
            .build();

        doReturn(ResponseEntity.ok().body(response)).when(adminService).getApprovalPendingVendors(0, 10);

        // When & Then
        mockMvc.perform(get("/admin/vendors/pending")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content.length()").value(2))
            .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void testUpdateVendorApproval_Approve_Success() throws Exception {
        // Given
        UUID vendorId = UUID.randomUUID();
        AdminToastDTO response = AdminToastDTO.builder().message("SUCCESS").build();
        doReturn(ResponseEntity.ok().body(response)).when(adminService).updateVendorApproval(vendorId, true);

        // When & Then
        mockMvc.perform(put("/admin/vendor/{id}/approve", vendorId)
                .param("approved", "true"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value("SUCCESS"));
    }

    @Test
    void testUpdateVendorApproval_Reject_Success() throws Exception {
        // Given
        UUID vendorId = UUID.randomUUID();
        AdminToastDTO response = AdminToastDTO.builder().message("SUCCESS").build();
        doReturn(ResponseEntity.ok().body(response)).when(adminService).updateVendorApproval(vendorId, false);

        // When & Then
        mockMvc.perform(put("/admin/vendor/{id}/approve", vendorId)
                .param("approved", "false"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value("SUCCESS"));
    }

    @Test
    void testDeleteVendor_Success() throws Exception {
        // Given
        UUID vendorId = UUID.randomUUID();
        AdminToastDTO response = AdminToastDTO.builder().message("SUCCESS").build();
        doReturn(ResponseEntity.ok().body(response)).when(adminService).deleteVendor(vendorId);

        // When & Then
        mockMvc.perform(delete("/admin/vendor/{id}", vendorId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value("SUCCESS"));
    }

    // Material Tests
    @Test
    void testGetAllMaterials_Success() throws Exception {
        // Given
        List<MaterialResponseAdminDTO> materials = Arrays.asList(
            createMaterialResponseAdminDTO("Material1", Unit.UNITS, PhaseType.ELECTRICAL),
            createMaterialResponseAdminDTO("Material2", Unit.KG, PhaseType.PLUMBING)
        );
        
        AdminPaginatedResponseDTO<MaterialResponseAdminDTO> response = AdminPaginatedResponseDTO.<MaterialResponseAdminDTO>builder()
            .content(materials)
            .pageNumber(0)
            .pageSize(10)
            .totalElements(2L)
            .totalPages(1)
            .build();

        doReturn(ResponseEntity.ok().body(response)).when(adminService).getAllMaterials(null, null, 0, 10);

        // When & Then
        mockMvc.perform(get("/admin/materials")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content.length()").value(2))
            .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void testGetAllMaterials_WithPhaseTypeFilter() throws Exception {
        // Given
        List<MaterialResponseAdminDTO> materials = Arrays.asList(
            createMaterialResponseAdminDTO("Electrical Material", Unit.UNITS, PhaseType.ELECTRICAL)
        );
        
        AdminPaginatedResponseDTO<MaterialResponseAdminDTO> response = AdminPaginatedResponseDTO.<MaterialResponseAdminDTO>builder()
            .content(materials)
            .pageNumber(0)
            .pageSize(10)
            .totalElements(1L)
            .totalPages(1)
            .build();

        doReturn(ResponseEntity.ok().body(response)).when(adminService).getAllMaterials(PhaseType.ELECTRICAL, null, 0, 10);

        // When & Then
        mockMvc.perform(get("/admin/materials")
                .param("phaseType", "ELECTRICAL")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void testGetAllMaterials_WithDeletedFilter() throws Exception {
        // Given
        List<MaterialResponseAdminDTO> materials = Arrays.asList(
            createMaterialResponseAdminDTO("Deleted Material", Unit.UNITS, PhaseType.ELECTRICAL)
        );
        
        AdminPaginatedResponseDTO<MaterialResponseAdminDTO> response = AdminPaginatedResponseDTO.<MaterialResponseAdminDTO>builder()
            .content(materials)
            .pageNumber(0)
            .pageSize(10)
            .totalElements(1L)
            .totalPages(1)
            .build();

        doReturn(ResponseEntity.ok().body(response)).when(adminService).getAllMaterials(null, true, 0, 10);

        // When & Then
        mockMvc.perform(get("/admin/materials")
                .param("deleted", "true")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void testGetMaterialById_Success() throws Exception {
        // Given
        UUID materialId = UUID.randomUUID();
        MaterialResponseAdminDTO material = createMaterialResponseAdminDTO("Test Material", Unit.UNITS, PhaseType.ELECTRICAL);
        doReturn(ResponseEntity.ok().body(material)).when(adminService).getMaterialByExposedId(materialId);

        // When & Then
        mockMvc.perform(get("/admin/materials/{id}", materialId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name").value("Test Material"))
            .andExpect(jsonPath("$.unit").value("UNITS"))
            .andExpect(jsonPath("$.phaseType").value("ELECTRICAL"));
    }

    @Test
    void testGetMaterialById_NotFound() throws Exception {
        // Given
        UUID materialId = UUID.randomUUID();
        doReturn(ResponseEntity.notFound().build()).when(adminService).getMaterialByExposedId(materialId);

        // When & Then
        mockMvc.perform(get("/admin/materials/{id}", materialId))
            .andExpect(status().isNotFound());
    }

    @Test
    void testAddMaterial_Success() throws Exception {
        // Given
        MaterialRequestAdminDTO request = new MaterialRequestAdminDTO();
        request.setName("New Material");
        request.setUnit(Unit.UNITS);
        request.setPhaseType(PhaseType.ELECTRICAL);
        request.setPricePerQuantity(15);

        AdminToastDTO response = AdminToastDTO.builder().message("SUCCESS").build();
        doReturn(ResponseEntity.ok().body(response)).when(adminService).addMaterial(any(MaterialRequestAdminDTO.class));

        // When & Then
        mockMvc.perform(post("/admin/materials")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value("SUCCESS"));
    }

    @Test
    void testAddMaterial_BadRequest() throws Exception {
        // Given
        MaterialRequestAdminDTO request = new MaterialRequestAdminDTO();
        request.setName("New Material");
        request.setUnit(Unit.UNITS);
        request.setPhaseType(PhaseType.ELECTRICAL);
        request.setPricePerQuantity(15);

        doReturn(ResponseEntity.badRequest().body("Data Integrity Violation")).when(adminService).addMaterial(any(MaterialRequestAdminDTO.class));

        // When & Then
        mockMvc.perform(post("/admin/materials")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateMaterial_Success() throws Exception {
        // Given
        UUID materialId = UUID.randomUUID();
        MaterialRequestAdminDTO request = new MaterialRequestAdminDTO();
        request.setName("Updated Material");
        request.setUnit(Unit.KG);
        request.setPhaseType(PhaseType.PLUMBING);
        request.setPricePerQuantity(20);

        AdminToastDTO response = AdminToastDTO.builder().message("SUCCESS").build();
        doReturn(ResponseEntity.ok().body(response)).when(adminService).updateMaterialByExposedId(eq(materialId), any(MaterialRequestAdminDTO.class));

        // When & Then
        mockMvc.perform(put("/admin/materials/{id}", materialId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value("SUCCESS"));
    }

    @Test
    void testUpdateMaterial_NotFound() throws Exception {
        // Given
        UUID materialId = UUID.randomUUID();
        MaterialRequestAdminDTO request = new MaterialRequestAdminDTO();
        request.setName("Updated Material");
        request.setUnit(Unit.KG);
        request.setPhaseType(PhaseType.PLUMBING);
        request.setPricePerQuantity(20);

        doReturn(ResponseEntity.notFound().build()).when(adminService).updateMaterialByExposedId(eq(materialId), any(MaterialRequestAdminDTO.class));

        // When & Then
        mockMvc.perform(put("/admin/materials/{id}", materialId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteMaterial_Success() throws Exception {
        // Given
        UUID materialId = UUID.randomUUID();
        AdminToastDTO response = AdminToastDTO.builder().message("SUCCESS").build();
        doReturn(ResponseEntity.ok().body(response)).when(adminService).deleteMaterialByExposedId(materialId);

        // When & Then
        mockMvc.perform(patch("/admin/materials/delete/{id}", materialId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value("SUCCESS"));
    }

    @Test
    void testDeleteMaterial_NotFound() throws Exception {
        // Given
        UUID materialId = UUID.randomUUID();
        doReturn(ResponseEntity.notFound().build()).when(adminService).deleteMaterialByExposedId(materialId);

        // When & Then
        mockMvc.perform(patch("/admin/materials/delete/{id}", materialId))
            .andExpect(status().isNotFound());
    }

    @Test
    void testReAddMaterial_Success() throws Exception {
        // Given
        UUID materialId = UUID.randomUUID();
        AdminToastDTO response = AdminToastDTO.builder().message("SUCCESS").build();
        doReturn(ResponseEntity.ok().body(response)).when(adminService).reAddMaterialByExposedId(materialId);

        // When & Then
        mockMvc.perform(patch("/admin/materials/re-add/{id}", materialId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value("SUCCESS"));
    }

    @Test
    void testReAddMaterial_NotFound() throws Exception {
        // Given
        UUID materialId = UUID.randomUUID();
        doReturn(ResponseEntity.notFound().build()).when(adminService).reAddMaterialByExposedId(materialId);

        // When & Then
        mockMvc.perform(patch("/admin/materials/re-add/{id}", materialId))
            .andExpect(status().isNotFound());
    }

    @Test
    void testHardDeleteMaterial_Success() throws Exception {
        // Given
        UUID materialId = UUID.randomUUID();
        AdminToastDTO response = AdminToastDTO.builder().message("SUCCESS").build();
        doReturn(ResponseEntity.ok().body(response)).when(adminService).hardDeleteMaterialByExposedId(materialId);

        // When & Then
        mockMvc.perform(delete("/admin/materials/hard/{id}", materialId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value("SUCCESS"));
    }

    @Test
    void testHardDeleteMaterial_NotFound() throws Exception {
        // Given
        UUID materialId = UUID.randomUUID();
        doReturn(ResponseEntity.notFound().build()).when(adminService).hardDeleteMaterialByExposedId(materialId);

        // When & Then
        mockMvc.perform(delete("/admin/materials/hard/{id}", materialId))
            .andExpect(status().isNotFound());
    }

    // Helper methods to create test DTOs
    private UserResponseAdminDTO createUserResponseAdminDTO(String email, String name) {
        UserResponseAdminDTO dto = new UserResponseAdminDTO();
        dto.setEmail(email);
        dto.setName(name);
        dto.setContact("1234567890");
        return dto;
    }

    private VendorResponseAdminDTO createVendorResponseAdminDTO(String email, String name) {
        VendorResponseAdminDTO dto = new VendorResponseAdminDTO();
        dto.setEmail(email);
        dto.setName(name);
        dto.setCompanyName(name + " Company");
        dto.setExperience("5 years");
        dto.setAvailable(true);
        dto.setApproved(true);
        return dto;
    }

    private MaterialResponseAdminDTO createMaterialResponseAdminDTO(String name, Unit unit, PhaseType phaseType) {
        MaterialResponseAdminDTO dto = new MaterialResponseAdminDTO();
        dto.setName(name);
        dto.setUnit(unit);
        dto.setPhaseType(phaseType);
        dto.setPricePerQuantity(10);
        dto.setDeleted(false);
        return dto;
    }
}
