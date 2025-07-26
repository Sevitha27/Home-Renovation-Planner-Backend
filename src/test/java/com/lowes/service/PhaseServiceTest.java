package com.lowes.service;

import com.lowes.convertor.PhaseConvertor;
import com.lowes.dto.request.PhaseRequestDTO;
import com.lowes.dto.response.PhaseResponse;
import com.lowes.dto.response.PhaseResponseDTO;
import com.lowes.entity.*;
import com.lowes.entity.enums.PhaseStatus;
import com.lowes.entity.enums.PhaseType;
import com.lowes.entity.enums.RenovationType;
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
    void getPhasesByRenovationType_shouldReturnMappedList() {
        phaseService.initRenovationPhaseMap();

        List<PhaseType> result = phaseService.getPhasesByRenovationType(RenovationType.KITCHEN_RENOVATION);
        assertThat(result).contains(PhaseType.CIVIL, PhaseType.ELECTRICAL);
    }
}
