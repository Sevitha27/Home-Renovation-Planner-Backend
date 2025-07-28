package com.lowes.service;

import com.lowes.convertor.PhaseConvertor;
import com.lowes.dto.request.PhaseRequestDTO;
import com.lowes.dto.response.PhaseMaterialUserResponse;
import com.lowes.dto.response.PhaseResponse;
import com.lowes.dto.response.PhaseResponseDTO;
import com.lowes.entity.*;
import com.lowes.entity.enums.PhaseStatus;
import com.lowes.entity.enums.PhaseType;
import com.lowes.entity.enums.RenovationType;
import com.lowes.mapper.PhaseMapper;
import com.lowes.repository.PhaseRepository;
import com.lowes.repository.RoomRepository;
import com.lowes.repository.VendorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class PhaseServiceTest {

    @InjectMocks
    private PhaseService phaseService;

    @Mock
    private PhaseRepository phaseRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private VendorRepository vendorRepository;

    private Room room;
    private Vendor vendor;
    private Phase phase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        room = new Room();
        room.setId(UUID.randomUUID());
        room.setExposedId(UUID.randomUUID());

        User user = new User();
        user.setName("Test Vendor");
        vendor = new Vendor();
        vendor.setId(12345);
        vendor.setExposedId(UUID.randomUUID());
        vendor.setUser(user);
        phase = new Phase();
        phase.setId(UUID.randomUUID());
        phase.setRoom(room);
        phase.setVendor(vendor);
        phase.setPhaseType(PhaseType.CIVIL);
        phase.setPhaseName("Civil Work");
        phase.setPhaseStatus(PhaseStatus.NOTSTARTED);
    }


    @Test
    void createPhase_shouldCreatePhaseSuccessfully() {
        PhaseRequestDTO dto = new PhaseRequestDTO();
        dto.setRoomId(room.getExposedId());
        dto.setVendorId(vendor.getExposedId());
        dto.setPhaseName("Civil Work");
        dto.setPhaseStatus(PhaseStatus.NOTSTARTED);
        dto.setPhaseType(PhaseType.CIVIL);
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusDays(5));
        dto.setDescription("Foundation");

        when(roomRepository.findByExposedId(dto.getRoomId())).thenReturn(Optional.of(room));
        when(vendorRepository.findByExposedId(dto.getVendorId())).thenReturn(vendor);
        when(phaseRepository.existsByRoomExposedIdAndPhaseType(dto.getRoomId(), dto.getPhaseType())).thenReturn(false);

        phaseService.createPhase(dto);

        verify(phaseRepository, times(1)).save(any(Phase.class));
    }

    @Test
    void getPhaseById_shouldReturnPhaseResponse() {
        // Arrange
        when(phaseRepository.findById(phase.getId())).thenReturn(Optional.of(phase));

        PhaseResponse expectedResponse = new PhaseResponse();
        expectedResponse.setId(phase.getId());
        expectedResponse.setPhaseName("Civil Work");

        mockStatic(PhaseConvertor.class);
        when(PhaseConvertor.phaseToPhaseResponse(phase)).thenReturn(expectedResponse);

        // Act
        PhaseResponse actual = phaseService.getPhaseById(phase.getId());

        // Assert
        assertNotNull(actual);
        assertEquals("Civil Work", actual.getPhaseName());
        assertEquals(phase.getId(), actual.getId());

        verify(phaseRepository).findById(phase.getId());
    }

    @Test
    void getPhaseById_shouldThrowIfNotFound() {
        UUID id = UUID.randomUUID();
        when(phaseRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> phaseService.getPhaseById(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Phase not found");
    }

    @Test
    void getPhasesByRoom_shouldReturnList() {
        UUID roomId = room.getExposedId();
        when(roomRepository.existsById(roomId)).thenReturn(true);
        when(phaseRepository.findAllByRoom_Id(roomId)).thenReturn(List.of(phase));

        List<PhaseResponseDTO> result = phaseService.getPhasesByRoom(roomId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPhaseName()).isEqualTo(phase.getPhaseName());
    }

    @Test
    void updatePhase_shouldUpdateValues() {
        UUID id = phase.getId();
        when(phaseRepository.findById(id)).thenReturn(Optional.of(phase));
        when(phaseRepository.save(any(Phase.class))).thenReturn(phase);

        PhaseRequestDTO update = new PhaseRequestDTO();
        update.setDescription("Updated desc");
        update.setPhaseName("New Name");

        Phase updated = phaseService.updatePhase(id, update);

        assertThat(updated.getDescription()).isEqualTo("Updated desc");
        assertThat(updated.getPhaseName()).isEqualTo("New Name");
    }

    @Test
    void deletePhase_shouldDelete() {
        UUID id = phase.getId();
        when(phaseRepository.findById(id)).thenReturn(Optional.of(phase));

        phaseService.deletePhase(id);

        verify(phaseRepository).delete(phase);
    }

    @Test
    void calculateTotalCost_shouldSumVendorAndMaterialCost() {
        PhaseMaterial pm1 = new PhaseMaterial();
        pm1.setTotalPrice(200);

        PhaseMaterial pm2 = new PhaseMaterial();
        pm2.setTotalPrice(300);

        phase.setVendorCost(500);
        phase.setPhaseMaterialList(List.of(pm1, pm2));

        when(phaseRepository.findById(phase.getId())).thenReturn(Optional.of(phase));

        int totalCost = phaseService.calculateTotalCost(phase.getId());

        assertThat(totalCost).isEqualTo(1000);
        verify(phaseRepository).save(phase);
    }
    @Test
    void updatePhase_shouldUpdateStartAndEndDate() {
        UUID id = phase.getId();
        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = LocalDate.now().plusDays(5);

        when(phaseRepository.findById(id)).thenReturn(Optional.of(phase));
        when(phaseRepository.save(any(Phase.class))).thenReturn(phase);

        PhaseRequestDTO update = new PhaseRequestDTO();
        update.setStartDate(start);
        update.setEndDate(end);

        Phase updated = phaseService.updatePhase(id, update);

        assertEquals(start, updated.getStartDate());
        assertEquals(end, updated.getEndDate());
    }
    @Test
    void updatePhase_shouldUpdatePhaseType() {
        UUID id = phase.getId();
        when(phaseRepository.findById(id)).thenReturn(Optional.of(phase));
        when(phaseRepository.save(any(Phase.class))).thenReturn(phase);

        PhaseRequestDTO update = new PhaseRequestDTO();
        update.setPhaseType(PhaseType.ELECTRICAL);

        Phase updated = phaseService.updatePhase(id, update);

        assertEquals(PhaseType.ELECTRICAL, updated.getPhaseType());
    }
    @Test
    void getAllPhaseMaterialsByPhaseId_shouldReturnEmptyListIfMaterialListIsNull() {
        phase.setPhaseMaterialList(null);

        when(phaseRepository.findById(phase.getId())).thenReturn(Optional.of(phase));

        List<PhaseMaterialUserResponse> result = phaseService.getAllPhaseMaterialsByPhaseId(phase.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void updatePhase_shouldUpdatePhaseStatus() {
        UUID id = phase.getId();
        when(phaseRepository.findById(id)).thenReturn(Optional.of(phase));
        when(phaseRepository.save(any(Phase.class))).thenReturn(phase);

        PhaseRequestDTO update = new PhaseRequestDTO();
        update.setPhaseStatus(PhaseStatus.COMPLETED);

        Phase updated = phaseService.updatePhase(id, update);

        assertEquals(PhaseStatus.COMPLETED, updated.getPhaseStatus());
    }

    @Test
    void setVendorCostForPhase_shouldSetCostIfVendorMatches() {
        UUID phaseId = phase.getId();
        UUID vendorId = vendor.getExposedId();
        phase.setVendorCost(null);

        when(phaseRepository.findById(phaseId)).thenReturn(Optional.of(phase));
        when(phaseRepository.save(any(Phase.class))).thenReturn(phase);

        PhaseResponse expectedResponse = new PhaseResponse();
        expectedResponse.setVendorCost(800);
        expectedResponse.setId(phaseId);
        expectedResponse.setPhaseName(phase.getPhaseName());

        try (MockedStatic<PhaseMapper> mockedMapper = mockStatic(PhaseMapper.class)) {
            mockedMapper.when(() -> PhaseMapper.toDTO(phase)).thenReturn(expectedResponse);

            PhaseResponse result = phaseService.setVendorCostForPhase(vendorId, phaseId, 800);

            assertEquals(800, result.getVendorCost());
            assertEquals(phaseId, result.getId());
            verify(phaseRepository).save(phase);
        }
    }

    @Test
    void getAllPhaseMaterialsByPhaseId_shouldReturnMaterialResponses() {

        Material material = new Material();
        material.setExposedId(UUID.randomUUID());

        PhaseMaterial pm = new PhaseMaterial();
        pm.setName("Bricks");
        pm.setTotalPrice(100);
        pm.setMaterial(material);
        pm.setPhase(phase);

        phase.setPhaseMaterialList(List.of(pm));

        pm.setName("Bricks");
        pm.setTotalPrice(100);

        phase.setPhaseMaterialList(List.of(pm));
        when(phaseRepository.findById(phase.getId())).thenReturn(Optional.of(phase));

        List<PhaseMaterialUserResponse> result = phaseService.getAllPhaseMaterialsByPhaseId(phase.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Bricks");
    }
    @Test
    void getPhasesByRoomExposedId_shouldReturnMappedPhases() {
        UUID roomExposedId = room.getExposedId();
        when(phaseRepository.findAllByRoom_ExposedId(roomExposedId)).thenReturn(List.of(phase));

        PhaseResponse mappedResponse = new PhaseResponse();
        mappedResponse.setId(phase.getId());

        try (MockedStatic<PhaseMapper> mocked = mockStatic(PhaseMapper.class)) {
            mocked.when(() -> PhaseMapper.toDTO(phase)).thenReturn(mappedResponse);

            List<PhaseResponse> result = phaseService.getPhasesByRoomExposedId(roomExposedId);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(phase.getId());
        }
    }
    @Test
    void getPhasesByRoom_shouldThrowIfRoomNotFound() {
        UUID roomId = UUID.randomUUID();
        when(roomRepository.existsById(roomId)).thenReturn(false);

        assertThatThrownBy(() -> phaseService.getPhasesByRoom(roomId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Room not found");
    }
    @Test
    void calculateTotalCost_shouldThrowIfPhaseNotFound() {
        UUID id = UUID.randomUUID();
        when(phaseRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> phaseService.calculateTotalCost(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Phase Not Found");
    }
    @Test
    void updatePhase_shouldThrowIfPhaseNotFound() {
        UUID id = UUID.randomUUID();
        when(phaseRepository.findById(id)).thenReturn(Optional.empty());

        PhaseRequestDTO update = new PhaseRequestDTO();
        update.setPhaseName("Updated");

        assertThatThrownBy(() -> phaseService.updatePhase(id, update))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Phase not found with id");
    }
    @Test
    void getPhasesByRenovationType_shouldReturnEmptyListIfUnknownType() {
        List<PhaseType> result = phaseService.getPhasesByRenovationType(null);
        assertThat(result).isEmpty();
    }

    @Test
    void deletePhase_shouldThrowIfPhaseNotFound() {
        UUID id = UUID.randomUUID();
        when(phaseRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> phaseService.deletePhase(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Phase not found with id");
    }

    @Test
    void getAllPhaseMaterialsByPhaseId_shouldThrowIfPhaseNotFound() {
        UUID id = UUID.randomUUID();
        when(phaseRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> phaseService.getAllPhaseMaterialsByPhaseId(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Phase not found");
    }

    @Test
    void calculateTotalCost_shouldHandleEmptyMaterialsAndNullVendorCost() {
        phase.setPhaseMaterialList(new ArrayList<>());
        phase.setVendorCost(null);

        when(phaseRepository.findById(phase.getId())).thenReturn(Optional.of(phase));

        int totalCost = phaseService.calculateTotalCost(phase.getId());

        assertThat(totalCost).isEqualTo(0);
        verify(phaseRepository).save(phase);
    }
    @Test
    void getPhasesByRoom_shouldThrowIfRoomDoesNotExist() {
        UUID roomId = UUID.randomUUID();
        when(roomRepository.existsById(roomId)).thenReturn(false);

        assertThatThrownBy(() -> phaseService.getPhasesByRoom(roomId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Room not found with ID");
    }

    @Test
    void createPhase_shouldThrowIfDuplicatePhaseTypeExists() {
        PhaseRequestDTO dto = new PhaseRequestDTO();
        dto.setRoomId(room.getExposedId());
        dto.setVendorId(vendor.getExposedId());
        dto.setPhaseType(PhaseType.CIVIL);

        when(roomRepository.findByExposedId(dto.getRoomId())).thenReturn(Optional.of(room));
        when(vendorRepository.findByExposedId(dto.getVendorId())).thenReturn(vendor);
        when(phaseRepository.existsByRoomExposedIdAndPhaseType(dto.getRoomId(), dto.getPhaseType())).thenReturn(true);

        assertThatThrownBy(() -> phaseService.createPhase(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists for this room");
    }

    @Test
    void setVendorCostForPhase_shouldThrowIfVendorMismatch() {
        UUID phaseId = phase.getId();
        UUID differentVendorId = UUID.randomUUID(); // Not matching

        when(phaseRepository.findById(phaseId)).thenReturn(Optional.of(phase));

        assertThatThrownBy(() -> phaseService.setVendorCostForPhase(differentVendorId, phaseId, 500))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unauthorized");
    }
    @Test
    void calculateTotalCost_shouldHandleNoMaterialsAndNullVendorCost() {
        phase.setVendorCost(null);
        phase.setPhaseMaterialList(Collections.emptyList());

        when(phaseRepository.findById(phase.getId())).thenReturn(Optional.of(phase));

        int total = phaseService.calculateTotalCost(phase.getId());

        assertEquals(0, total);
        verify(phaseRepository).save(phase);
    }

    @Test
    void setVendorCostForPhase_shouldThrowIfVendorIsNull() {
        phase.setVendor(null);
        UUID phaseId = phase.getId();

        when(phaseRepository.findById(phaseId)).thenReturn(Optional.of(phase));

        assertThatThrownBy(() -> phaseService.setVendorCostForPhase(UUID.randomUUID(), phaseId, 500))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unauthorized");
    }


    @Test
    void getPhasesByRenovationType_shouldReturnMappedList() {
        phaseService.initRenovationPhaseMap();

        List<PhaseType> result = phaseService.getPhasesByRenovationType(RenovationType.KITCHEN_RENOVATION);
        assertThat(result).contains(PhaseType.CIVIL, PhaseType.ELECTRICAL);
    }
}
