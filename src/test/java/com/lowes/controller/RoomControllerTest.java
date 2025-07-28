package com.lowes.controller;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowes.dto.request.RoomRequestDTO;
import com.lowes.dto.response.RoomResponseDTO;
import com.lowes.entity.Room;
import com.lowes.entity.User;
import com.lowes.entity.enums.RenovationType;
import com.lowes.exception.ElementNotFoundException;
import com.lowes.service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class RoomControllerTest {

    @Mock
    private RoomService roomService;

    @InjectMocks
    private RoomController roomController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private UUID projectId;
    private UUID roomId;
    private UUID userExposedId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(roomController).build();
        objectMapper = new ObjectMapper();
        projectId = UUID.randomUUID();
        roomId = UUID.randomUUID();
        userExposedId = UUID.randomUUID();

        User mockUser = User.builder().exposedId(userExposedId).build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(mockUser, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void createRoom_Success() throws Exception {
        RoomRequestDTO request = new RoomRequestDTO("Living Room", RenovationType.LIVING_ROOM_REMODEL, projectId);
        Room mockRoom = Room.builder()
                .exposedId(roomId)
                .name("Living Room")
                .renovationType(RenovationType.LIVING_ROOM_REMODEL)
                .phases(new ArrayList<>())
                .totalCost(0)
                .build();

        when(roomService.createRoom(any(RoomRequestDTO.class))).thenReturn(mockRoom);

        mockMvc.perform(post("/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exposedId").value(roomId.toString()))
                .andExpect(jsonPath("$.name").value("Living Room"))
                .andExpect(jsonPath("$.renovationType").value("LIVING_ROOM_REMODEL"));

        verify(roomService, times(1)).createRoom(any(RoomRequestDTO.class));
    }

    @Test
    void createRoom_ProjectExists_ReturnsCreatedRoom() throws Exception {
        UUID projectExposedId = UUID.randomUUID();
        UUID roomExposedId = UUID.randomUUID();

        // Prepare request DTO
        RoomRequestDTO requestDTO = new RoomRequestDTO();
        requestDTO.setName("Test Room");
        requestDTO.setProjectExposedId(projectExposedId);


        Room mockRoom = new Room();
        mockRoom.setExposedId(roomExposedId);
        mockRoom.setName("Test Room");
        mockRoom.setRenovationType(RenovationType.KITCHEN_RENOVATION);
        mockRoom.setPhases(List.of());
        mockRoom.setTotalCost(10000);
        when(roomService.createRoom(any(RoomRequestDTO.class))).thenReturn(mockRoom);

        mockMvc.perform(post("/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Room"));
    }


    @Test
    void getRoomById_Success() throws Exception {
        Room mockRoom = Room.builder()
                .exposedId(roomId)
                .name("Bedroom")
                .renovationType(RenovationType.BEDROOM_RENOVATION)
                .phases(new ArrayList<>())
                .totalCost(0)
                .build();

        when(roomService.findByExposedId(roomId)).thenReturn(mockRoom);

        mockMvc.perform(get("/rooms/{exposedId}", roomId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exposedId").value(roomId.toString()))
                .andExpect(jsonPath("$.name").value("Bedroom"))
                .andExpect(jsonPath("$.renovationType").value("BEDROOM_RENOVATION"));
    }


    @Test
    void getRoomsByProject_Success() throws Exception {
        Room room1 = Room.builder().exposedId(UUID.randomUUID()).name("Living Room").renovationType(RenovationType.LIVING_ROOM_REMODEL).phases(new ArrayList<>()).totalCost(0).build();
        Room room2 = Room.builder().exposedId(UUID.randomUUID()).name("Kitchen").renovationType(RenovationType.KITCHEN_RENOVATION).phases(new ArrayList<>()).totalCost(0).build();

        when(roomService.getRoomsByProject(projectId)).thenReturn(List.of(room1, room2));

        mockMvc.perform(get("/rooms/project/{projectExposedId}", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void updateRoom_Success() throws Exception {
        RoomRequestDTO request = new RoomRequestDTO("Updated Room", RenovationType.FULL_HOME_RENOVATION, null);
        Room mockRoom = Room.builder()
                .exposedId(roomId)
                .name("Updated Room")
                .renovationType(RenovationType.FULL_HOME_RENOVATION)
                .phases(new ArrayList<>())
                .totalCost(0)
                .build();

        when(roomService.updateRoom(eq(roomId), any(RoomRequestDTO.class))).thenReturn(mockRoom);

        mockMvc.perform(put("/rooms/{exposedId}", roomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Room"))
                .andExpect(jsonPath("$.renovationType").value("FULL_HOME_RENOVATION"));
    }


    @Test
    void deleteRoom_Success() throws Exception {
        doNothing().when(roomService).deleteRoom(roomId);

        mockMvc.perform(delete("/rooms/{exposedId}", roomId))
                .andExpect(status().isOk());

        verify(roomService, times(1)).deleteRoom(roomId);
    }


}