package com.lowes.repository;

import com.lowes.entity.Project;
import com.lowes.entity.User;
import com.lowes.entity.enums.Role;
import com.lowes.entity.enums.ServiceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Transactional
class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User testUser;
    private Project testProject;
    private UUID testExposedId;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setRole(Role.CUSTOMER);
        testUser.setExposedId(UUID.randomUUID());
        entityManager.persistAndFlush(testUser);

        testExposedId = UUID.randomUUID();

        testProject = Project.builder()
                .name("Test Project")
                .serviceType(ServiceType.WHOLE_HOUSE)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(3))
                .estimatedBudget(75000)
                .exposedId(testExposedId)
                .owner(testUser)
                .build();

        entityManager.persistAndFlush(testProject);
    }

    @Test
    @Rollback
    void whenValidOwnerId_thenProjectsShouldBeFound() {

        List<Project> foundProjects = projectRepository.findByOwnerId(testUser.getId());
        assertEquals(1, foundProjects.size());
        assertEquals("Test Project", foundProjects.get(0).getName());
        assertEquals(testUser.getId(), foundProjects.get(0).getOwner().getId());
    }

    @Test
    @Rollback
    void whenInvalidOwnerId_thenEmptyListShouldReturn() {
        List<Project> foundProjects = projectRepository.findByOwnerId(999L);
        assertTrue(foundProjects.isEmpty());
    }

    @Test
    @Rollback
    void whenValidExposedId_thenProjectShouldBeFound() {
        Optional<Project> foundProject = projectRepository.findByExposedId(testExposedId);
        assertTrue(foundProject.isPresent());
        assertEquals("Test Project", foundProject.get().getName());
        assertEquals(testExposedId, foundProject.get().getExposedId());
    }

    @Test
    @Rollback
    void whenInvalidExposedId_thenProjectShouldNotBeFound() {
        Optional<Project> foundProject = projectRepository.findByExposedId(UUID.randomUUID());
        assertTrue(foundProject.isEmpty());
    }

    @Test
    @Rollback
    void whenValidExposedIdAndOwnerId_thenProjectShouldBeFound() {
        Optional<Project> foundProject = projectRepository.findByExposedIdAndOwnerId(
                testExposedId,
                testUser.getId()
        );
        assertTrue(foundProject.isPresent());
        assertEquals(testProject.getId(), foundProject.get().getId());
    }

    @Test
    @Rollback
    void whenMismatchedOwnerId_thenProjectShouldNotBeFound() {
        User otherUser = new User();
        otherUser.setEmail("other@example.com");
        otherUser.setExposedId(UUID.randomUUID());
        entityManager.persistAndFlush(otherUser);
        Optional<Project> foundProject = projectRepository.findByExposedIdAndOwnerId(
                testExposedId,
                otherUser.getId()
        );
        assertTrue(foundProject.isEmpty());
    }

    @Test
    @Rollback
    void whenValidExposedIdAndOwnerExposedId_thenShouldExist() {
        boolean exists = projectRepository.existsByExposedIdAndOwnerExposedId(
                testExposedId,
                testUser.getExposedId()
        );
        assertTrue(exists);
    }

    @Test
    @Rollback
    void whenInvalidCombination_thenShouldNotExist() {
        boolean exists = projectRepository.existsByExposedIdAndOwnerExposedId(
                UUID.randomUUID(),
                testUser.getExposedId()
        );
        assertFalse(exists);
    }

    @Test
    @Rollback
    void whenSavingProject_thenShouldPersistCorrectly() {
        Project newProject = Project.builder()
                .name("New Project")
                .serviceType(ServiceType.ROOM_WISE)
                .exposedId(UUID.randomUUID())
                .owner(testUser)
                .build();
        Project savedProject = projectRepository.save(newProject);
        entityManager.flush();
        entityManager.clear();
        Project fetched = entityManager.find(Project.class, savedProject.getId());
        assertEquals("New Project", fetched.getName());
        assertNotNull(fetched.getCreatedAt());
        assertNotNull(fetched.getUpdatedAt());
    }

    @Test
    @Rollback
    void whenUpdatingProject_thenShouldUpdateCorrectly() {
        testProject.setName("Updated Project");
        testProject.setEstimatedBudget(100000);
        projectRepository.save(testProject);
        entityManager.flush();
        entityManager.clear();
        Project updated = entityManager.find(Project.class, testProject.getId());
        assertEquals("Updated Project", updated.getName());
        assertEquals(100000, updated.getEstimatedBudget());
        assertTrue(updated.getUpdatedAt().isAfter(updated.getCreatedAt()));
    }

    @Test
    @Rollback
    void whenDeletingProject_thenShouldBeRemoved() {
        projectRepository.delete(testProject);
        entityManager.flush();
        Project deleted = entityManager.find(Project.class, testProject.getId());
        assertNull(deleted);
    }

    @Test
    @Rollback
    void whenSavingWithoutExposedId_thenShouldGenerateAutomatically() {
        Project project = new Project();
        project.setName("Auto ID Project");
        project.setOwner(testUser);
        project.setServiceType(ServiceType.WHOLE_HOUSE);
        Project saved = projectRepository.save(project);
        assertNotNull(saved.getExposedId());
    }

    @Test
    @Rollback
    void whenDuplicateExposedId_thenShouldFail() {
        Project duplicateProject = Project.builder()
                .name("Duplicate Project")
                .exposedId(testExposedId)
                .owner(testUser)
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> {
            projectRepository.saveAndFlush(duplicateProject);
        });
    }
}