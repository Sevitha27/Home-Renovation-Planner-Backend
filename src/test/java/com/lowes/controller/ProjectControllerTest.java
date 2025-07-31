package com.lowes.controller;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.lowes.mapper.ProjectMapper;
import org.springframework.security.core.Authentication;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lowes.dto.response.ProjectResponseDTO;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowes.dto.request.ProjectRequestDTO;
import com.lowes.entity.Project;
import com.lowes.entity.User;
import com.lowes.entity.enums.ServiceType;
import com.lowes.service.ProjectService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectController projectController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(projectController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    private Project getMockProject() {
        Project project = new Project();
        project.setExposedId(UUID.randomUUID());
        project.setName("Test Project");
        project.setServiceType(ServiceType.ROOM_WISE);
        project.setEstimatedBudget(100000);
        project.setStartDate(LocalDate.now());
        project.setEndDate(LocalDate.now().plusDays(30));

        User owner = new User();
        owner.setExposedId(UUID.randomUUID());
        owner.setName("Muskan");

        project.setOwner(owner);
        project.setRooms(new ArrayList<>());

        return project;
    }

    @Test
    void testCreateProject() throws Exception {

        ProjectRequestDTO requestDTO = new ProjectRequestDTO();
        requestDTO.setName("Test Project");
        requestDTO.setEstimatedBudget(50000);
        requestDTO.setServiceType(ServiceType.ROOM_WISE);
        requestDTO.setStartDate(LocalDate.of(2025, 7, 1));
        requestDTO.setEndDate(LocalDate.of(2025, 7, 30));
        User dummyUser = new User();
        dummyUser.setId(1L);
        dummyUser.setExposedId(UUID.randomUUID());
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(dummyUser);
        when(projectService.createProject(any(ProjectRequestDTO.class), eq(dummyUser.getExposedId())))
                .thenReturn(getMockProject());
        ProjectResponseDTO response = projectController.createProject(requestDTO, authentication);
        assertEquals("Test Project", response.getName());
    }


    @Test
    void testGetProjectById() throws Exception {
        UUID testId = UUID.randomUUID();

        Project mockProject = new Project();
        mockProject.setExposedId(testId);
        mockProject.setName("Test Project");
        mockProject.setServiceType(ServiceType.ROOM_WISE);
        mockProject.setEstimatedBudget(40000);
        mockProject.setStartDate(LocalDate.of(2025, 7, 1));
        mockProject.setEndDate(LocalDate.of(2025, 7, 30));

        when(projectService.getProjectById(testId)).thenReturn(mockProject);

        mockMvc.perform(get("/projects/{id}", testId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Project"));
    }


    @Test
    void testGetAllProjectsByUser() throws Exception {
        List<Project> projectList = List.of(getMockProject());
        when(projectService.getProjectsByUser(any(Long.class))).thenReturn(projectList);
        User mockUser = new User();
        mockUser.setId(1L);
        TestingAuthenticationToken authentication = new TestingAuthenticationToken(mockUser, null);

        mockMvc.perform(get("/projects/user")
                        .principal(authentication))  // <- inject mock authentication
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Project"));

    }


    @Test
    void testUpdateProject() throws Exception {
        UUID projectId = UUID.randomUUID();

        ProjectRequestDTO requestDTO = new ProjectRequestDTO();
        requestDTO.setName("Updated Project");
        requestDTO.setEstimatedBudget(60000);
        requestDTO.setServiceType(ServiceType.WHOLE_HOUSE);
        requestDTO.setStartDate(LocalDate.of(2025, 8, 1));
        requestDTO.setEndDate(LocalDate.of(2025, 8, 30));

        Project project = getMockProject(); // Project entity
        project.setName("Updated Project");
        ProjectResponseDTO responseDTO = ProjectMapper.toDTO(project);

        when(projectService.updateProject(eq(projectId), any(ProjectRequestDTO.class)))
                .thenReturn(project);

        mockMvc.perform(put("/projects/{exposedId}", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Project"));
    }

    @Test
    void testDeleteProject() throws Exception {
        String validExposedId = "123e4567-e89b-12d3-a456-426614174000";

        mockMvc.perform(delete("/projects/{exposedId}", validExposedId))
                .andExpect(status().isOk());
    }

}