package com.lowes.service;

import com.lowes.dto.response.BudgetOverviewResponseDTO;
import com.lowes.entity.Phase;
import com.lowes.entity.Project;
import com.lowes.entity.Room;
import com.lowes.entity.User;
import com.lowes.entity.enums.PhaseType;
import com.lowes.exception.AccessDeniedException;
import com.lowes.exception.NotFoundException;
import com.lowes.repository.PhaseRepository;
import com.lowes.repository.ProjectRepository;
import com.lowes.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;
import org.mockito.Mockito;


@Nested
@ExtendWith(MockitoExtension.class)
class budgetOverviewServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private PhaseRepository phaseRepository;

    @InjectMocks
    private BudgetOverviewService budgetOverviewService;

    private UUID projectId;
    private UUID userId;
    private User mockUser;
    private Project mockProject;

    @BeforeEach
    void setUp() {
        projectId = UUID.randomUUID();
        userId = UUID.randomUUID();

        mockUser = new User();
        mockUser.setExposedId(userId);

        mockProject = new Project();
        mockProject.setExposedId(projectId);
        mockProject.setOwner(mockUser);
        mockProject.setName("Test Project");
        mockProject.setEstimatedBudget(50000);
        mockProject.setTotalCost(30000);
    }

    @Test
    void testGetBudgetOverview_Success() {
        // Rooms
        Room room1 = new Room();
        room1.setId(UUID.randomUUID());
        room1.setName("Kitchen");
        room1.setTotalCost(15000);

        Room room2 = new Room();
        room2.setId(UUID.randomUUID());
        room2.setName("Living Room");
        room2.setTotalCost(15000);

        //mockProject.setRooms(Arrays.asList(room1, room2));


        // Phases
        Phase phase1 = new Phase();
        phase1.setId(UUID.randomUUID());
        phase1.setPhaseName("Wiring");
        phase1.setPhaseType(PhaseType.ELECTRICAL);
        phase1.setVendorCost(5000);
        phase1.setTotalPhaseMaterialCost(2000);
        phase1.setTotalPhaseCost(7000);

        Phase phase2 = new Phase();
        phase2.setId(UUID.randomUUID());
        phase2.setPhaseName("Plumbing");
        phase2.setPhaseType(PhaseType.PLUMBING);
        phase2.setVendorCost(4000);
        phase2.setTotalPhaseMaterialCost(1000);
        phase2.setTotalPhaseCost(5000);

        Project spyProject = Mockito.spy(mockProject);
        doReturn(30000).when(spyProject).getTotalCost();

        // Mocking
        when(projectRepository.findByExposedId(projectId)).thenReturn(Optional.of(spyProject));
        when(roomRepository.findByProjectExposedId(projectId)).thenReturn(Arrays.asList(room1, room2));
        when(phaseRepository.findAllByRoom_Id(room1.getId())).thenReturn(Collections.singletonList(phase1));
        when(phaseRepository.findAllByRoom_Id(room2.getId())).thenReturn(Collections.singletonList(phase2));

//        ReflectionTestUtils.setField(mockProject, "totalCost", 30000);

        // Call service
        BudgetOverviewResponseDTO response = budgetOverviewService.getBudgetOverview(projectId, userId);

        // Validate
        assertNotNull(response);
        assertEquals("Test Project", response.getProjectName());
        assertEquals(50000, response.getEstimatedBudget());
        assertEquals(30000, response.getTotalActualCost());

        // Rooms
        assertEquals(2, response.getRooms().size());
        assertTrue(
                response.getRooms().stream().anyMatch(r -> r.getRoomName().equals("Kitchen"))
        );

        // Phases
        assertEquals(2, response.getPhases().size());
        assertTrue(
                response.getPhases().stream().anyMatch(p -> p.getPhaseName().equals("Wiring"))
        );
        assertTrue(
                response.getPhases().stream().anyMatch(p -> p.getPhaseType().equals("ELECTRICAL"))
        );
    }

    @Test
    void testGetBudgetOverview_ProjectNotFound() {
        when(projectRepository.findByExposedId(projectId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                budgetOverviewService.getBudgetOverview(projectId, userId)
        );
    }

    @Test
    void testGetBudgetOverview_AccessDenied() {
        // Different owner
        User anotherUser = new User();
        anotherUser.setExposedId(UUID.randomUUID());
        mockProject.setOwner(anotherUser);

        when(projectRepository.findByExposedId(projectId)).thenReturn(Optional.of(mockProject));

        assertThrows(AccessDeniedException.class, () ->
                budgetOverviewService.getBudgetOverview(projectId, userId)
        );
    }

    @Test
    void testGetBudgetOverview_NoPhasesForRooms() {
        // Rooms without phases
        Room room = new Room();
        room.setId(UUID.randomUUID());
        room.setName("Empty Room");
        room.setTotalCost(0);

        when(projectRepository.findByExposedId(projectId)).thenReturn(Optional.of(mockProject));
        when(roomRepository.findByProjectExposedId(projectId)).thenReturn(Collections.singletonList(room));
        when(phaseRepository.findAllByRoom_Id(room.getId())).thenReturn(Collections.emptyList());

        BudgetOverviewResponseDTO response = budgetOverviewService.getBudgetOverview(projectId, userId);

        assertNotNull(response);
        assertEquals(1, response.getRooms().size());
        assertEquals(0, response.getPhases().size());
    }

    @Test
    void testGetBudgetOverview_ProjectTotalCostNull() {
        // Project with null total cost
        mockProject.setTotalCost(null);

        Room room = new Room();
        room.setId(UUID.randomUUID());
        room.setName("Test Room");
        room.setTotalCost(5000);

        when(projectRepository.findByExposedId(projectId)).thenReturn(Optional.of(mockProject));
        when(roomRepository.findByProjectExposedId(projectId)).thenReturn(Collections.singletonList(room));
        when(phaseRepository.findAllByRoom_Id(room.getId())).thenReturn(Collections.emptyList());

        BudgetOverviewResponseDTO response = budgetOverviewService.getBudgetOverview(projectId, userId);

        assertNotNull(response);
        assertEquals(0, response.getTotalActualCost()); // should default to 0 if null
    }

    @Test
    void testGetBudgetOverview_NoRooms() {
        // No rooms returned
        when(projectRepository.findByExposedId(projectId)).thenReturn(Optional.of(mockProject));
        when(roomRepository.findByProjectExposedId(projectId)).thenReturn(Collections.emptyList());

        BudgetOverviewResponseDTO response = budgetOverviewService.getBudgetOverview(projectId, userId);

        assertNotNull(response);
        assertEquals(0, response.getRooms().size());
        assertEquals(0, response.getPhases().size());
    }

    @Test
    void testGetBudgetOverview_MixedRoomsWithAndWithoutPhases() {
        // Room 1 has phases, Room 2 has none
        Room room1 = new Room();
        room1.setId(UUID.randomUUID());
        room1.setName("Kitchen");
        room1.setTotalCost(10000);

        Room room2 = new Room();
        room2.setId(UUID.randomUUID());
        room2.setName("Empty Room");
        room2.setTotalCost(5000);

        Phase phase = new Phase();
        phase.setId(UUID.randomUUID());
        phase.setPhaseName("Wiring");
        phase.setPhaseType(PhaseType.ELECTRICAL);
        phase.setVendorCost(2000);
        phase.setTotalPhaseMaterialCost(1000);
        phase.setTotalPhaseCost(3000);

        when(projectRepository.findByExposedId(projectId)).thenReturn(Optional.of(mockProject));
        when(roomRepository.findByProjectExposedId(projectId)).thenReturn(Arrays.asList(room1, room2));
        when(phaseRepository.findAllByRoom_Id(room1.getId())).thenReturn(Collections.singletonList(phase));
        when(phaseRepository.findAllByRoom_Id(room2.getId())).thenReturn(Collections.emptyList());

        BudgetOverviewResponseDTO response = budgetOverviewService.getBudgetOverview(projectId, userId);

        assertNotNull(response);
        assertEquals(2, response.getRooms().size()); // both rooms
        assertEquals(1, response.getPhases().size()); // only one phase from room1
    }

    @Test
    void testGetBudgetOverview_RoomTotalCostNull() {
        Room room = new Room();
        room.setId(UUID.randomUUID());
        room.setName("NullCostRoom");
        room.setTotalCost(null); // triggers branch

        when(projectRepository.findByExposedId(projectId)).thenReturn(Optional.of(mockProject));
        when(roomRepository.findByProjectExposedId(projectId)).thenReturn(Collections.singletonList(room));
        when(phaseRepository.findAllByRoom_Id(room.getId())).thenReturn(Collections.emptyList());

        BudgetOverviewResponseDTO response = budgetOverviewService.getBudgetOverview(projectId, userId);
        assertEquals(0, response.getRooms().get(0).getTotalRoomCost());
    }

    @Test
    void testGetBudgetOverview_PhaseCostsNull() {
        Room room = new Room();
        room.setId(UUID.randomUUID());
        room.setName("RoomWithNullPhase");

        Phase phase = new Phase();
        phase.setId(UUID.randomUUID());
        phase.setPhaseName("Wiring");
        phase.setPhaseType(PhaseType.ELECTRICAL);
        phase.setVendorCost(null); // triggers branch
        phase.setTotalPhaseMaterialCost(null); // triggers branch
        phase.setTotalPhaseCost(null); // triggers branch

        when(projectRepository.findByExposedId(projectId)).thenReturn(Optional.of(mockProject));
        when(roomRepository.findByProjectExposedId(projectId)).thenReturn(Collections.singletonList(room));
        when(phaseRepository.findAllByRoom_Id(room.getId())).thenReturn(Collections.singletonList(phase));

        BudgetOverviewResponseDTO response = budgetOverviewService.getBudgetOverview(projectId, userId);

        assertEquals(0, response.getPhases().get(0).getVendorCost());
        assertEquals(0, response.getPhases().get(0).getMaterialCost());
        assertEquals(0, response.getPhases().get(0).getTotalPhaseCost());
    }

    @Test
    void testGetBudgetOverview_ProjectBudgetAndTotalCostNullTogether() {
        // estimatedBudget and totalCost null
        mockProject.setEstimatedBudget(null);
        mockProject.setTotalCost(null);

        Room room = new Room();
        room.setId(UUID.randomUUID());
        room.setName("NullBudgetRoom");
        room.setTotalCost(1000);

        when(projectRepository.findByExposedId(projectId)).thenReturn(Optional.of(mockProject));
        when(roomRepository.findByProjectExposedId(projectId)).thenReturn(Collections.singletonList(room));
        when(phaseRepository.findAllByRoom_Id(room.getId())).thenReturn(Collections.emptyList());

        BudgetOverviewResponseDTO response = budgetOverviewService.getBudgetOverview(projectId, userId);

        // Both should default to 0
        assertEquals(0, response.getEstimatedBudget());
        assertEquals(0, response.getTotalActualCost());
    }

    @Test
    void testGetBudgetOverview_PhaseTotalCostFallback() {
        Room room = new Room();
        room.setId(UUID.randomUUID());
        room.setName("FallbackRoom");

        Phase phase = new Phase();
        phase.setId(UUID.randomUUID());
        phase.setPhaseName("FallbackPhase");
        phase.setPhaseType(PhaseType.PLUMBING);
        phase.setVendorCost(2000);
        phase.setTotalPhaseMaterialCost(3000);
        phase.setTotalPhaseCost(null); // triggers fallback vendor+material sum

        when(projectRepository.findByExposedId(projectId)).thenReturn(Optional.of(mockProject));
        when(roomRepository.findByProjectExposedId(projectId)).thenReturn(Collections.singletonList(room));
        when(phaseRepository.findAllByRoom_Id(room.getId())).thenReturn(Collections.singletonList(phase));

        BudgetOverviewResponseDTO response = budgetOverviewService.getBudgetOverview(projectId, userId);

        // Should calculate 2000 + 3000 = 5000
        assertEquals(5000, response.getPhases().get(0).getTotalPhaseCost());
    }

    @Test
    void testGetBudgetOverview_NoRoomsAndBudgetCostNull() {
        // Both estimatedBudget and totalCost null
        mockProject.setEstimatedBudget(null);
        mockProject.setTotalCost(null);

        // No rooms
        when(projectRepository.findByExposedId(projectId)).thenReturn(Optional.of(mockProject));
        when(roomRepository.findByProjectExposedId(projectId)).thenReturn(Collections.emptyList());

        BudgetOverviewResponseDTO response = budgetOverviewService.getBudgetOverview(projectId, userId);

        // Verify
        assertEquals(0, response.getEstimatedBudget());
        assertEquals(0, response.getTotalActualCost());
        assertEquals(0, response.getRooms().size());
        assertEquals(0, response.getPhases().size());
    }


}