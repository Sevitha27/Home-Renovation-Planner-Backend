package com.lowes.service;

import com.lowes.dto.request.admin.MaterialRequestAdminDTO;
import com.lowes.dto.response.admin.AdminPaginatedResponseDTO;
import com.lowes.dto.response.admin.AdminToastDTO;
import com.lowes.dto.response.admin.MaterialResponseAdminDTO;
import com.lowes.dto.response.admin.UserResponseAdminDTO;
import com.lowes.dto.response.admin.VendorResponseAdminDTO;
import com.lowes.entity.*;
import com.lowes.entity.enums.PhaseType;
import com.lowes.entity.enums.Role;
import com.lowes.entity.enums.Unit;
import com.lowes.exception.ElementNotFoundException;
import com.lowes.exception.OperationNotAllowedException;
import com.lowes.mapper.AdminConverter;
import com.lowes.repository.MaterialRepository;
import com.lowes.repository.SkillRepository;
import com.lowes.repository.UserRepository;
import com.lowes.repository.VendorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.MockedStatic;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.TransactionStatus;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class AdminServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private VendorRepository vendorRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private MaterialRepository materialRepository;
    @Mock
    private AdminConverter adminConverter;
    @InjectMocks
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // getAllCustomers
    @Test
    void testGetAllCustomers_success() {
        User user = new User();
        user.setRole(Role.CUSTOMER);
        Page<User> page = new PageImpl<>(List.of(user));
        when(userRepository.findByRole(eq(Role.CUSTOMER), any(Pageable.class))).thenReturn(page);
        when(adminConverter.usertoUserResponseAdminDTO(any(User.class))).thenReturn(new UserResponseAdminDTO());
        ResponseEntity<?> response = adminService.getAllCustomers(0, 10);
        assertEquals(200, response.getStatusCodeValue());
    }
    @Test
    void testGetAllCustomers_exception() {
        when(userRepository.findByRole(eq(Role.CUSTOMER), any(Pageable.class))).thenThrow(new RuntimeException());
        ResponseEntity<?> response = adminService.getAllCustomers(0, 10);
        assertEquals(500, response.getStatusCodeValue());
    }

    // deleteUser
    @Test
    void testDeleteUser_userNotFound() {
        when(userRepository.findByExposedId(any(UUID.class))).thenReturn(null);
        ResponseEntity<?> response = adminService.deleteUser(UUID.randomUUID());
        assertEquals(400, response.getStatusCodeValue());
        Object body = response.getBody();
        assertNotNull(body);
        if (body instanceof AdminToastDTO dto) {
            assertEquals("ERROR", dto.getMessage());
        } else {
            assertTrue(body.toString().contains("ERROR"));
        }
    }
    @Test
    void testDeleteUser_success() {
        User user = new User();
        user.setProjects(new ArrayList<>());
        user.setVendorsServingThisUser(new ArrayList<>());
        user.setVendorReviews(new ArrayList<>());
        when(userRepository.findByExposedId(any(UUID.class))).thenReturn(user);
        doNothing().when(userRepository).delete(any(User.class));
        ResponseEntity<?> response = adminService.deleteUser(UUID.randomUUID());
        assertEquals(200, response.getStatusCodeValue());
    }
    @Test
    void testDeleteUser_exception() {
        try (MockedStatic<TransactionAspectSupport> tasMock = mockStatic(TransactionAspectSupport.class)) {
            TransactionStatus mockStatus = mock(TransactionStatus.class);
            tasMock.when(TransactionAspectSupport::currentTransactionStatus).thenReturn(mockStatus);
            when(userRepository.findByExposedId(any(UUID.class))).thenThrow(new RuntimeException());
            ResponseEntity<?> response = adminService.deleteUser(UUID.randomUUID());
            assertEquals(500, response.getStatusCodeValue());
        }
    }

    // getApprovedVendors
    @Test
    void testGetApprovedVendors_success() {
        Vendor vendor = new Vendor();
        Page<Vendor> page = new PageImpl<>(List.of(vendor));
        when(vendorRepository.findByApproved(eq(true), any(Pageable.class))).thenReturn(page);
        when(adminConverter.vendorToVendorResponseAdminDTO(any(Vendor.class))).thenReturn(new VendorResponseAdminDTO());
        ResponseEntity<?> response = adminService.getApprovedVendors(0, 10);
        assertEquals(200, response.getStatusCodeValue());
    }
    @Test
    void testGetApprovedVendors_exception() {
        when(vendorRepository.findByApproved(eq(true), any(Pageable.class))).thenThrow(new RuntimeException());
        ResponseEntity<?> response = adminService.getApprovedVendors(0, 10);
        assertEquals(500, response.getStatusCodeValue());
    }

    // getApprovalPendingVendors
    @Test
    void testGetApprovalPendingVendors_success() {
        Vendor vendor = new Vendor();
        Page<Vendor> page = new PageImpl<>(List.of(vendor));
        when(vendorRepository.findByApprovedIsNull(any(Pageable.class))).thenReturn(page);
        when(adminConverter.vendorToVendorResponseAdminDTO(any(Vendor.class))).thenReturn(new VendorResponseAdminDTO());
        ResponseEntity<?> response = adminService.getApprovalPendingVendors(0, 10);
        assertEquals(200, response.getStatusCodeValue());
    }
    @Test
    void testGetApprovalPendingVendors_exception() {
        when(vendorRepository.findByApprovedIsNull(any(Pageable.class))).thenThrow(new RuntimeException());
        ResponseEntity<?> response = adminService.getApprovalPendingVendors(0, 10);
        assertEquals(500, response.getStatusCodeValue());
    }

    // updateVendorApproval
    @Test
    void testUpdateVendorApproval_vendorNotFound() {
        when(vendorRepository.findByExposedId(any(UUID.class))).thenReturn(null);
        ResponseEntity<?> response = adminService.updateVendorApproval(UUID.randomUUID(), true);
        assertEquals(400, response.getStatusCodeValue());
    }
    @Test
    void testUpdateVendorApproval_approveSuccess() {
        Vendor vendor = new Vendor();
        when(vendorRepository.findByExposedId(any(UUID.class))).thenReturn(vendor);
        when(vendorRepository.save(any(Vendor.class))).thenReturn(vendor);
        ResponseEntity<?> response = adminService.updateVendorApproval(UUID.randomUUID(), true);
        assertEquals(200, response.getStatusCodeValue());
    }
    @Test
    void testUpdateVendorApproval_rejectSuccess() {
        Vendor vendor = new Vendor();
        when(vendorRepository.findByExposedId(any(UUID.class))).thenReturn(vendor);
        // Mock all dependencies used in deleteVendor
        vendor.setSkills(new ArrayList<>());
        vendor.setCustomers(new ArrayList<>());
        when(skillRepository.findSkillsWithNoVendors()).thenReturn(new ArrayList<>());
        doNothing().when(skillRepository).deleteAll(anyList());
        doNothing().when(vendorRepository).delete(any(Vendor.class));
        ResponseEntity<?> response = adminService.updateVendorApproval(UUID.randomUUID(), false);
        assertEquals(200, response.getStatusCodeValue());
    }
    @Test
    void testUpdateVendorApproval_exception() {
        try (MockedStatic<TransactionAspectSupport> tasMock = mockStatic(TransactionAspectSupport.class)) {
            TransactionStatus mockStatus = mock(TransactionStatus.class);
            tasMock.when(TransactionAspectSupport::currentTransactionStatus).thenReturn(mockStatus);
            when(vendorRepository.findByExposedId(any(UUID.class))).thenThrow(new RuntimeException());
            ResponseEntity<?> response = adminService.updateVendorApproval(UUID.randomUUID(), true);
            assertEquals(500, response.getStatusCodeValue());
        }
    }

    // deleteVendor
    @Test
    void testDeleteVendor_vendorNotFound() {
        when(vendorRepository.findByExposedId(any(UUID.class))).thenReturn(null);
        ResponseEntity<?> response = adminService.deleteVendor(UUID.randomUUID());
        assertEquals(400, response.getStatusCodeValue());
    }
    @Test
    void testDeleteVendor_success() {
        Vendor vendor = new Vendor();
        vendor.setSkills(new ArrayList<>());
        vendor.setCustomers(new ArrayList<>());
        when(vendorRepository.findByExposedId(any(UUID.class))).thenReturn(vendor);
        when(skillRepository.findSkillsWithNoVendors()).thenReturn(new ArrayList<>());
        doNothing().when(skillRepository).deleteAll(anyList());
        doNothing().when(vendorRepository).delete(any(Vendor.class));
        ResponseEntity<?> response = adminService.deleteVendor(UUID.randomUUID());
        assertEquals(200, response.getStatusCodeValue());
    }
    @Test
    void testDeleteVendor_exception() {
        try (MockedStatic<TransactionAspectSupport> tasMock = mockStatic(TransactionAspectSupport.class)) {
            TransactionStatus mockStatus = mock(TransactionStatus.class);
            tasMock.when(TransactionAspectSupport::currentTransactionStatus).thenReturn(mockStatus);
            when(vendorRepository.findByExposedId(any(UUID.class))).thenThrow(new RuntimeException());
            ResponseEntity<?> response = adminService.deleteVendor(UUID.randomUUID());
            assertEquals(500, response.getStatusCodeValue());
        }
    }

    // getAllMaterials
    @Test
    void testGetAllMaterials_success() {
        Material material = new Material();
        Page<Material> page = new PageImpl<>(List.of(material));
        when(materialRepository.findAll(any(Pageable.class))).thenReturn(page);
        try (MockedStatic<AdminConverter> adminConverterMock = mockStatic(AdminConverter.class)) {
            adminConverterMock.when(() -> AdminConverter.materialToMaterialAdminResponse(any(Material.class))).thenReturn(new MaterialResponseAdminDTO());
            ResponseEntity<?> response = adminService.getAllMaterials(null, null, 0, 10);
            assertEquals(200, response.getStatusCodeValue());
        }
    }
    @Test
    void testGetAllMaterials_exception() {
        when(materialRepository.findAll(any(Pageable.class))).thenThrow(new RuntimeException());
        try (MockedStatic<AdminConverter> adminConverterMock = mockStatic(AdminConverter.class)) {
            adminConverterMock.when(() -> AdminConverter.materialToMaterialAdminResponse(any(Material.class))).thenReturn(new MaterialResponseAdminDTO());
            ResponseEntity<?> response = adminService.getAllMaterials(null, null, 0, 10);
            assertEquals(500, response.getStatusCodeValue());
        }
    }

    // getMaterialByExposedId
    @Test
    void testGetMaterialByExposedId_success() {
        Material material = new Material();
        when(materialRepository.findByExposedId(any(UUID.class))).thenReturn(Optional.of(material));
        try (MockedStatic<AdminConverter> adminConverterMock = mockStatic(AdminConverter.class)) {
            adminConverterMock.when(() -> AdminConverter.materialToMaterialAdminResponse(any(Material.class))).thenReturn(new MaterialResponseAdminDTO());
            ResponseEntity<?> response = adminService.getMaterialByExposedId(UUID.randomUUID());
            assertEquals(200, response.getStatusCodeValue());
        }
    }
    @Test
    void testGetMaterialByExposedId_notFound() {
        when(materialRepository.findByExposedId(any(UUID.class))).thenReturn(Optional.empty());
        try (MockedStatic<AdminConverter> adminConverterMock = mockStatic(AdminConverter.class)) {
            adminConverterMock.when(() -> AdminConverter.materialToMaterialAdminResponse(any(Material.class))).thenReturn(new MaterialResponseAdminDTO());
            ResponseEntity<?> response = adminService.getMaterialByExposedId(UUID.randomUUID());
            assertEquals(404, response.getStatusCodeValue());
        }
    }
    @Test
    void testGetMaterialByExposedId_exception() {
        when(materialRepository.findByExposedId(any(UUID.class))).thenThrow(new RuntimeException());
        try (MockedStatic<AdminConverter> adminConverterMock = mockStatic(AdminConverter.class)) {
            adminConverterMock.when(() -> AdminConverter.materialToMaterialAdminResponse(any(Material.class))).thenReturn(new MaterialResponseAdminDTO());
            ResponseEntity<?> response = adminService.getMaterialByExposedId(UUID.randomUUID());
            assertEquals(500, response.getStatusCodeValue());
        }
    }

    // addMaterial
    @Test
    void testAddMaterial_success() {
        MaterialRequestAdminDTO dto = new MaterialRequestAdminDTO();
        Material material = new Material();
        try (MockedStatic<AdminConverter> adminConverterMock = mockStatic(AdminConverter.class)) {
            adminConverterMock.when(() -> AdminConverter.materialAdminRequestToMaterial(any(MaterialRequestAdminDTO.class))).thenReturn(material);
            when(materialRepository.save(any(Material.class))).thenReturn(material);
            ResponseEntity<?> response = adminService.addMaterial(dto);
            assertEquals(200, response.getStatusCodeValue());
        }
    }
    @Test
    void testAddMaterial_dataIntegrityViolation() {
        MaterialRequestAdminDTO dto = new MaterialRequestAdminDTO();
        try (MockedStatic<AdminConverter> adminConverterMock = mockStatic(AdminConverter.class)) {
            adminConverterMock.when(() -> AdminConverter.materialAdminRequestToMaterial(any(MaterialRequestAdminDTO.class))).thenReturn(new Material());
            when(materialRepository.save(any(Material.class))).thenThrow(new DataIntegrityViolationException("error"));
            try (MockedStatic<TransactionAspectSupport> tasMock = mockStatic(TransactionAspectSupport.class)) {
                TransactionStatus mockStatus = mock(TransactionStatus.class);
                tasMock.when(TransactionAspectSupport::currentTransactionStatus).thenReturn(mockStatus);
                ResponseEntity<?> response = adminService.addMaterial(dto);
                assertEquals(400, response.getStatusCodeValue());
            }
        }
    }
    @Test
    void testAddMaterial_exception() {
        MaterialRequestAdminDTO dto = new MaterialRequestAdminDTO();
        try (MockedStatic<AdminConverter> adminConverterMock = mockStatic(AdminConverter.class)) {
            adminConverterMock.when(() -> AdminConverter.materialAdminRequestToMaterial(any(MaterialRequestAdminDTO.class))).thenReturn(new Material());
            when(materialRepository.save(any(Material.class))).thenThrow(new RuntimeException());
            try (MockedStatic<TransactionAspectSupport> tasMock = mockStatic(TransactionAspectSupport.class)) {
                TransactionStatus mockStatus = mock(TransactionStatus.class);
                tasMock.when(TransactionAspectSupport::currentTransactionStatus).thenReturn(mockStatus);
                ResponseEntity<?> response = adminService.addMaterial(dto);
                assertEquals(500, response.getStatusCodeValue());
            }
        }
    }

    // updateMaterialByExposedId
    @Test
    void testUpdateMaterialByExposedId_success() {
        UUID id = UUID.randomUUID();
        MaterialRequestAdminDTO dto = new MaterialRequestAdminDTO();
        Material material = new Material();
        when(materialRepository.findByExposedId(eq(id))).thenReturn(Optional.of(material));
        when(materialRepository.save(any(Material.class))).thenReturn(material);
        ResponseEntity<?> response = adminService.updateMaterialByExposedId(id, dto);
        assertEquals(200, response.getStatusCodeValue());
    }
    @Test
    void testUpdateMaterialByExposedId_notFound() {
        UUID id = UUID.randomUUID();
        MaterialRequestAdminDTO dto = new MaterialRequestAdminDTO();
        when(materialRepository.findByExposedId(eq(id))).thenReturn(Optional.empty());
        try (MockedStatic<TransactionAspectSupport> tasMock = mockStatic(TransactionAspectSupport.class)) {
            TransactionStatus mockStatus = mock(TransactionStatus.class);
            tasMock.when(TransactionAspectSupport::currentTransactionStatus).thenReturn(mockStatus);
            ResponseEntity<?> response = adminService.updateMaterialByExposedId(id, dto);
            assertEquals(404, response.getStatusCodeValue());
        }
    }
    @Test
    void testUpdateMaterialByExposedId_dataIntegrityViolation() {
        UUID id = UUID.randomUUID();
        MaterialRequestAdminDTO dto = new MaterialRequestAdminDTO();
        Material material = new Material();
        when(materialRepository.findByExposedId(eq(id))).thenReturn(Optional.of(material));
        when(materialRepository.save(any(Material.class))).thenThrow(new DataIntegrityViolationException("error"));
        try (MockedStatic<TransactionAspectSupport> tasMock = mockStatic(TransactionAspectSupport.class)) {
            TransactionStatus mockStatus = mock(TransactionStatus.class);
            tasMock.when(TransactionAspectSupport::currentTransactionStatus).thenReturn(mockStatus);
            ResponseEntity<?> response = adminService.updateMaterialByExposedId(id, dto);
            assertEquals(400, response.getStatusCodeValue());
        }
    }
    @Test
    void testUpdateMaterialByExposedId_exception() {
        UUID id = UUID.randomUUID();
        MaterialRequestAdminDTO dto = new MaterialRequestAdminDTO();
        when(materialRepository.findByExposedId(eq(id))).thenThrow(new RuntimeException());
        try (MockedStatic<TransactionAspectSupport> tasMock = mockStatic(TransactionAspectSupport.class)) {
            TransactionStatus mockStatus = mock(TransactionStatus.class);
            tasMock.when(TransactionAspectSupport::currentTransactionStatus).thenReturn(mockStatus);
            ResponseEntity<?> response = adminService.updateMaterialByExposedId(id, dto);
            assertEquals(500, response.getStatusCodeValue());
        }
    }

    // deleteMaterialByExposedId
    @Test
    void testDeleteMaterialByExposedId_success() {
        UUID id = UUID.randomUUID();
        Material material = new Material();
        material.setDeleted(false);
        when(materialRepository.findByExposedId(eq(id))).thenReturn(Optional.of(material));
        when(materialRepository.save(any(Material.class))).thenReturn(material);
        ResponseEntity<?> response = adminService.deleteMaterialByExposedId(id);
        assertEquals(200, response.getStatusCodeValue());
    }
    @Test
    void testDeleteMaterialByExposedId_notFound() {
        UUID id = UUID.randomUUID();
        when(materialRepository.findByExposedId(eq(id))).thenReturn(Optional.empty());
        try (MockedStatic<TransactionAspectSupport> tasMock = mockStatic(TransactionAspectSupport.class)) {
            TransactionStatus mockStatus = mock(TransactionStatus.class);
            tasMock.when(TransactionAspectSupport::currentTransactionStatus).thenReturn(mockStatus);
            ResponseEntity<?> response = adminService.deleteMaterialByExposedId(id);
            assertEquals(404, response.getStatusCodeValue());
        }
    }
    @Test
    void testDeleteMaterialByExposedId_alreadyDeleted() {
        UUID id = UUID.randomUUID();
        Material material = new Material();
        material.setDeleted(true);
        when(materialRepository.findByExposedId(eq(id))).thenReturn(Optional.of(material));
        try (MockedStatic<TransactionAspectSupport> tasMock = mockStatic(TransactionAspectSupport.class)) {
            TransactionStatus mockStatus = mock(TransactionStatus.class);
            tasMock.when(TransactionAspectSupport::currentTransactionStatus).thenReturn(mockStatus);
            ResponseEntity<?> response = adminService.deleteMaterialByExposedId(id);
            assertEquals(400, response.getStatusCodeValue());
        }
    }
    @Test
    void testDeleteMaterialByExposedId_exception() {
        UUID id = UUID.randomUUID();
        when(materialRepository.findByExposedId(eq(id))).thenThrow(new RuntimeException());
        try (MockedStatic<TransactionAspectSupport> tasMock = mockStatic(TransactionAspectSupport.class)) {
            TransactionStatus mockStatus = mock(TransactionStatus.class);
            tasMock.when(TransactionAspectSupport::currentTransactionStatus).thenReturn(mockStatus);
            ResponseEntity<?> response = adminService.deleteMaterialByExposedId(id);
            assertEquals(500, response.getStatusCodeValue());
        }
    }

    // reAddMaterialByExposedId
    @Test
    void testReAddMaterialByExposedId_success() {
        UUID id = UUID.randomUUID();
        Material material = new Material();
        material.setDeleted(true);
        when(materialRepository.findByExposedId(eq(id))).thenReturn(Optional.of(material));
        when(materialRepository.save(any(Material.class))).thenReturn(material);
        ResponseEntity<?> response = adminService.reAddMaterialByExposedId(id);
        assertEquals(200, response.getStatusCodeValue());
    }
    @Test
    void testReAddMaterialByExposedId_notFound() {
        UUID id = UUID.randomUUID();
        when(materialRepository.findByExposedId(eq(id))).thenReturn(Optional.empty());
        try (MockedStatic<TransactionAspectSupport> tasMock = mockStatic(TransactionAspectSupport.class)) {
            TransactionStatus mockStatus = mock(TransactionStatus.class);
            tasMock.when(TransactionAspectSupport::currentTransactionStatus).thenReturn(mockStatus);
            ResponseEntity<?> response = adminService.reAddMaterialByExposedId(id);
            assertEquals(404, response.getStatusCodeValue());
        }
    }
    @Test
    void testReAddMaterialByExposedId_notDeleted() {
        UUID id = UUID.randomUUID();
        Material material = new Material();
        material.setDeleted(false);
        when(materialRepository.findByExposedId(eq(id))).thenReturn(Optional.of(material));
        try (MockedStatic<TransactionAspectSupport> tasMock = mockStatic(TransactionAspectSupport.class)) {
            TransactionStatus mockStatus = mock(TransactionStatus.class);
            tasMock.when(TransactionAspectSupport::currentTransactionStatus).thenReturn(mockStatus);
            ResponseEntity<?> response = adminService.reAddMaterialByExposedId(id);
            assertEquals(400, response.getStatusCodeValue());
        }
    }
    @Test
    void testReAddMaterialByExposedId_exception() {
        UUID id = UUID.randomUUID();
        when(materialRepository.findByExposedId(eq(id))).thenThrow(new RuntimeException());
        try (MockedStatic<TransactionAspectSupport> tasMock = mockStatic(TransactionAspectSupport.class)) {
            TransactionStatus mockStatus = mock(TransactionStatus.class);
            tasMock.when(TransactionAspectSupport::currentTransactionStatus).thenReturn(mockStatus);
            ResponseEntity<?> response = adminService.reAddMaterialByExposedId(id);
            assertEquals(500, response.getStatusCodeValue());
        }
    }

    // hardDeleteMaterialByExposedId
    @Test
    void testHardDeleteMaterialByExposedId_success() {
        UUID id = UUID.randomUUID();
        Material material = new Material();
        when(materialRepository.findByExposedId(eq(id))).thenReturn(Optional.of(material));
        doNothing().when(materialRepository).delete(any(Material.class));
        ResponseEntity<?> response = adminService.hardDeleteMaterialByExposedId(id);
        assertEquals(200, response.getStatusCodeValue());
    }
    @Test
    void testHardDeleteMaterialByExposedId_notFound() {
        UUID id = UUID.randomUUID();
        when(materialRepository.findByExposedId(eq(id))).thenReturn(Optional.empty());
        ResponseEntity<?> response = adminService.hardDeleteMaterialByExposedId(id);
        assertEquals(404, response.getStatusCodeValue());
    }
    @Test
    void testHardDeleteMaterialByExposedId_exception() {
        UUID id = UUID.randomUUID();
        when(materialRepository.findByExposedId(eq(id))).thenThrow(new RuntimeException());
        try (MockedStatic<TransactionAspectSupport> tasMock = mockStatic(TransactionAspectSupport.class)) {
            TransactionStatus mockStatus = mock(TransactionStatus.class);
            tasMock.when(TransactionAspectSupport::currentTransactionStatus).thenReturn(mockStatus);
            ResponseEntity<?> response = adminService.hardDeleteMaterialByExposedId(id);
            assertEquals(500, response.getStatusCodeValue());
        }
    }
}
