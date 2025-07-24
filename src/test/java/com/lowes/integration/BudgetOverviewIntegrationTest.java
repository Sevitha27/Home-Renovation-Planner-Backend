package com.lowes.integration;

import com.lowes.entity.*;
import com.lowes.entity.enums.PhaseStatus;
import com.lowes.entity.enums.PhaseType;
import com.lowes.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import com.lowes.config.TestConfig;

import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(TestConfig.class) // disables security during this test
public class BudgetOverviewIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectRepository projectRepo;
    @Autowired
    private RoomRepository roomRepo;
    @Autowired
    private PhaseRepository phaseRepo;
    @Autowired
    private UserRepository userRepo;

    private UUID projectId;
    private UUID userId;

    @BeforeEach
    void setup() {
        // Clean DB
        phaseRepo.deleteAll();
        roomRepo.deleteAll();
        projectRepo.deleteAll();
        userRepo.deleteAll();

        // Persist user
        User user = new User();
        userId = UUID.randomUUID();
        user.setExposedId(userId);
        user.setName("Integration User");
        user.setEmail("test@example.com");
        user = userRepo.save(user);

        // Persist project
        Project project = new Project();
        projectId = UUID.randomUUID();
        project.setExposedId(projectId);
        project.setName("Integration Test Project");
        project.setEstimatedBudget(60000);
        project.setTotalCost(25000);
        project.setOwner(user);
        projectRepo.save(project);

        // Persist room
        Room room = new Room();
        room.setName("Kitchen");
        room.setProject(project);
        room.setTotalCost(10000);
        roomRepo.save(room);

        // Persist phase
        Phase phase = new Phase();
        phase.setRoom(room);
        phase.setPhaseName("Plumbing");
        phase.setPhaseType(PhaseType.PLUMBING);
        phase.setVendorCost(4000);
        phase.setTotalPhaseMaterialCost(2000);
        phase.setTotalPhaseCost(6000);
        phase.setStartDate(LocalDate.now().plusDays(5));
        phase.setEndDate(LocalDate.now().plusDays(10));
        phase.setPhaseStatus(PhaseStatus.INPROGRESS);
        phaseRepo.save(phase);
    }

    @Test
    void testGetBudgetOverview_Success() throws Exception {
        mockMvc.perform(get("/projects/" + projectId + "/budget-overview")
                        .header("userId", userId.toString()))
                .andDo(print()) // See actual response
                .andExpect(status().isOk()); // Only assert status
    }
}
