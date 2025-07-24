package com.lowes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowes.dto.request.ProjectRequestDTO;
import com.lowes.dto.response.ProjectResponseDTO;
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
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {

    @InjectMocks
    private ProjectController projectController;

    @Mock
    private ProjectService projectService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private UUID projectExposedId;
    private Long userId;
    private User user;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(projectController).build();
        objectMapper = new ObjectMapper().findAndRegisterModules();

        projectExposedId = UUID.randomUUID();
        userId = 123L;

        user = new User();
        user.setId(userId);
        user.setExposedId(UUID.randomUUID());
    }

    @Test
    void createProject_shouldReturnCreatedProject() throws Exception {
        ProjectRequestDTO requestDTO = new ProjectRequestDTO();
        requestDTO.setName("Modular Kitchen");
        requestDTO.setServiceType(ServiceType.ROOM_WISE);

        ProjectResponseDTO responseDTO = new ProjectResponseDTO();
        responseDTO.setName("Modular Kitchen");
        responseDTO.setServiceType(ServiceType.ROOM_WISE);
        responseDTO.setExposedId(projectExposedId.toString());
        responseDTO.setOwnerId(user.getExposedId().toString());

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
       // when(projectService.createProject(any(), eq(user.getExposedId().toString())))
              //  .thenReturn(responseDTO);

        mockMvc.perform(post("/projects")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Modular Kitchen")))
                .andExpect(jsonPath("$.serviceType", is("ROOM_WISE")));
    }

    @Test
    void getUserProjects_shouldReturnList() throws Exception {
        ProjectResponseDTO responseDTO = new ProjectResponseDTO();
        responseDTO.setName("Living Room");
        responseDTO.setServiceType(ServiceType.ROOM_WISE);
        responseDTO.setExposedId(projectExposedId.toString());
        responseDTO.setOwnerId(user.getExposedId().toString());

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
      //  when(projectService.getProjectsByUser(user.getId())).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/projects/user")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("Living Room")))
                .andExpect(jsonPath("$[0].serviceType", is("ROOM_WISE")));
    }

    @Test
    void updateProject_shouldReturnUpdatedProject() throws Exception {
        ProjectRequestDTO requestDTO = new ProjectRequestDTO();
        requestDTO.setName("Updated Room");
        requestDTO.setServiceType(ServiceType.WHOLE_HOUSE);

        ProjectResponseDTO responseDTO = new ProjectResponseDTO();
        responseDTO.setName("Updated Room");
        responseDTO.setServiceType(ServiceType.WHOLE_HOUSE);
        responseDTO.setExposedId(projectExposedId.toString());

     //   when(projectService.updateProject(eq(projectExposedId), any())).thenReturn(responseDTO);

        mockMvc.perform(put("/projects/{exposedId}", projectExposedId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Room")))
                .andExpect(jsonPath("$.serviceType", is("WHOLE_HOUSE")));
    }

    @Test
    void getProject_shouldReturnProject() throws Exception {
        ProjectResponseDTO responseDTO = new ProjectResponseDTO();
        responseDTO.setName("Dining Area");
        responseDTO.setServiceType(ServiceType.ROOM_WISE);
        responseDTO.setExposedId(projectExposedId.toString());

      //  when(projectService.getProjectById(projectExposedId)).thenReturn(responseDTO);


        mockMvc.perform(get("/projects/{exposedId}", projectExposedId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Dining Area")))
                .andExpect(jsonPath("$.serviceType", is("ROOM_WISE")));
    }

    @Test
    void deleteProject_shouldCallService() throws Exception {
        doNothing().when(projectService).deleteProject(projectExposedId);

        mockMvc.perform(delete("/projects/{exposedId}", projectExposedId))
                .andExpect(status().isOk());

        verify(projectService, times(1)).deleteProject(projectExposedId);
    }
}
