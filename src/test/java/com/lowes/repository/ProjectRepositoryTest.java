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
        // When
        List<Project> foundProjects = projectRepository.findByOwnerId(testUser.getId());

        // Then
        assertEquals(1, foundProjects.size());
        assertEquals("Test Project", foundProjects.get(0).getName());
        assertEquals(testUser.getId(), foundProjects.get(0).getOwner().getId());
    }

    @Test
    @Rollback
    void whenInvalidOwnerId_thenEmptyListShouldReturn() {
        // When
        List<Project> foundProjects = projectRepository.findByOwnerId(999L);

        // Then
        assertTrue(foundProjects.isEmpty());
    }

    @Test
    @Rollback
    void whenValidExposedId_thenProjectShouldBeFound() {
        // When
        Optional<Project> foundProject = projectRepository.findByExposedId(testExposedId);

        // Then
        assertTrue(foundProject.isPresent());
        assertEquals("Test Project", foundProject.get().getName());
        assertEquals(testExposedId, foundProject.get().getExposedId());
    }

    @Test
    @Rollback
    void whenInvalidExposedId_thenProjectShouldNotBeFound() {
        // When
        Optional<Project> foundProject = projectRepository.findByExposedId(UUID.randomUUID());

        // Then
        assertTrue(foundProject.isEmpty());
    }

    @Test
    @Rollback
    void whenValidExposedIdAndOwnerId_thenProjectShouldBeFound() {
        // When
        Optional<Project> foundProject = projectRepository.findByExposedIdAndOwnerId(
                testExposedId,
                testUser.getId()
        );

        // Then
        assertTrue(foundProject.isPresent());
        assertEquals(testProject.getId(), foundProject.get().getId());
    }

    @Test
    @Rollback
    void whenMismatchedOwnerId_thenProjectShouldNotBeFound() {
        // Given
        User otherUser = new User();
        otherUser.setEmail("other@example.com");
        otherUser.setExposedId(UUID.randomUUID());
        entityManager.persistAndFlush(otherUser);

        // When
        Optional<Project> foundProject = projectRepository.findByExposedIdAndOwnerId(
                testExposedId,
                otherUser.getId()
        );

        // Then
        assertTrue(foundProject.isEmpty());
    }

    @Test
    @Rollback
    void whenValidExposedIdAndOwnerExposedId_thenShouldExist() {
        // When
        boolean exists = projectRepository.existsByExposedIdAndOwnerExposedId(
                testExposedId,
                testUser.getExposedId()
        );

        // Then
        assertTrue(exists);
    }

    @Test
    @Rollback
    void whenInvalidCombination_thenShouldNotExist() {
        // When
        boolean exists = projectRepository.existsByExposedIdAndOwnerExposedId(
                UUID.randomUUID(),
                testUser.getExposedId()
        );

        // Then
        assertFalse(exists);
    }

    @Test
    @Rollback
    void whenSavingProject_thenShouldPersistCorrectly() {
        // Given
        Project newProject = Project.builder()
                .name("New Project")
                .serviceType(ServiceType.ROOM_WISE)
                .exposedId(UUID.randomUUID())
                .owner(testUser)
                .build();

        // When
        Project savedProject = projectRepository.save(newProject);
        entityManager.flush();
        entityManager.clear();

        // Then
        Project fetched = entityManager.find(Project.class, savedProject.getId());
        assertEquals("New Project", fetched.getName());
        assertNotNull(fetched.getCreatedAt());
        assertNotNull(fetched.getUpdatedAt());
    }

    @Test
    @Rollback
    void whenUpdatingProject_thenShouldUpdateCorrectly() {
        // Given
        testProject.setName("Updated Project");
        testProject.setEstimatedBudget(100000);

        // When
        projectRepository.save(testProject);
        entityManager.flush();
        entityManager.clear();

        // Then
        Project updated = entityManager.find(Project.class, testProject.getId());
        assertEquals("Updated Project", updated.getName());
        assertEquals(100000, updated.getEstimatedBudget());
        assertTrue(updated.getUpdatedAt().isAfter(updated.getCreatedAt()));
    }

    @Test
    @Rollback
    void whenDeletingProject_thenShouldBeRemoved() {
        // When
        projectRepository.delete(testProject);
        entityManager.flush();

        // Then
        Project deleted = entityManager.find(Project.class, testProject.getId());
        assertNull(deleted);
    }

    @Test
    @Rollback
    void whenSavingWithoutExposedId_thenShouldGenerateAutomatically() {
        // Given
        Project project = new Project();
        project.setName("Auto ID Project");
        project.setOwner(testUser);
        project.setServiceType(ServiceType.WHOLE_HOUSE);

        // When
        Project saved = projectRepository.save(project);

        // Then
        assertNotNull(saved.getExposedId());
    }

    @Test
    @Rollback
    void whenDuplicateExposedId_thenShouldFail() {
        Project duplicateProject = Project.builder()
                .name("Duplicate Project")
                .exposedId(testExposedId)  // Same as existing
                .owner(testUser)
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> {
            projectRepository.saveAndFlush(duplicateProject);
        });
    }
}