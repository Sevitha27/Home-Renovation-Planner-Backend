package com.lowes.service;

import com.lowes.dto.request.ProjectRequestDTO;
import com.lowes.entity.Project;
import com.lowes.entity.User;
import com.lowes.exception.ElementNotFoundException;
import com.lowes.repository.ProjectRepository;
import com.lowes.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testCreateProject_success() {
        UUID userId = UUID.randomUUID();
        User mockUser = new User();
        when(userRepository.findByExposedId(userId)).thenReturn(mockUser);

        ProjectRequestDTO dto = new ProjectRequestDTO();
        dto.setName("New Home Project");
        dto.setServiceType(null);
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusDays(30));
        dto.setEstimatedBudget(50000);

        Project savedProject = new Project();
        when(projectRepository.save(any(Project.class))).thenReturn(savedProject);

        Project result = projectService.createProject(dto, userId);
        assertNotNull(result);
        verify(projectRepository).save(any(Project.class));
    }


    @Test
    void testUpdateProject_success() {
        UUID projectId = UUID.randomUUID();
        Project existingProject = new Project();
        when(projectRepository.findByExposedId(projectId)).thenReturn(Optional.of(existingProject));

        ProjectRequestDTO dto = new ProjectRequestDTO();
        dto.setName("Updated Project");
        dto.setServiceType(null);
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusDays(10));
        dto.setEstimatedBudget(20000);

        when(projectRepository.save(any(Project.class))).thenReturn(existingProject);

        Project result = projectService.updateProject(projectId, dto);
        assertEquals("Updated Project", result.getName());
        verify(projectRepository).save(existingProject);
    }

    @Test
    void testUpdateProject_notFound() {
        UUID projectId = UUID.randomUUID();
        when(projectRepository.findByExposedId(projectId)).thenReturn(Optional.empty());

        ProjectRequestDTO dto = new ProjectRequestDTO();

        assertThrows(ElementNotFoundException.class, () -> {
            projectService.updateProject(projectId, dto);
        });
    }


    @Test
    void testGetProjectById_success() {
        UUID id = UUID.randomUUID();
        Project project = new Project();
        when(projectRepository.findByExposedId(id)).thenReturn(Optional.of(project));

        Project result = projectService.getProjectById(id);
        assertNotNull(result);
    }


    @Test
    void testGetProjectById_notFound() {
        UUID id = UUID.randomUUID();
        when(projectRepository.findByExposedId(id)).thenReturn(Optional.empty());

        assertThrows(ElementNotFoundException.class, () -> {
            projectService.getProjectById(id);
        });
    }


    @Test
    void testGetProjectsByUser_success() {
        Long userId = 1L;
        List<Project> mockProjects = List.of(new Project());
        when(projectRepository.findByOwnerId(userId)).thenReturn(mockProjects);

        List<Project> result = projectService.getProjectsByUser(userId);
        assertEquals(1, result.size());
    }


    @Test
    void testDeleteProject_success() {
        UUID id = UUID.randomUUID();
        Project mockProject = new Project();
        when(projectRepository.findByExposedId(id)).thenReturn(Optional.of(mockProject));

        projectService.deleteProject(id);

        verify(projectRepository).delete(mockProject);
    }


    @Test
    void testDeleteProject_notFound() {
        UUID id = UUID.randomUUID();
        when(projectRepository.findByExposedId(id)).thenReturn(Optional.empty());

        assertThrows(ElementNotFoundException.class, () -> {
            projectService.deleteProject(id);
        });
    }
}