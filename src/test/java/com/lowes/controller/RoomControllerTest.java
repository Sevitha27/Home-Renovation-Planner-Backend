package com.lowes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowes.dto.request.RoomRequestDTO;
import com.lowes.dto.response.PhaseResponseDTO;
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
import java.util.Arrays;
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
    private UUID userExposedId; // Changed to UUID for consistency with User entity

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(roomController).build();
        objectMapper = new ObjectMapper();
        projectId = UUID.randomUUID();
        roomId = UUID.randomUUID();
        userExposedId = UUID.randomUUID(); // Initialize userExposedId

        // Mock Authentication for @PreAuthorize
        User mockUser = User.builder().exposedId(userExposedId).build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(mockUser, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void createRoom_Success() throws Exception {
        RoomRequestDTO request = new RoomRequestDTO("Living Room", RenovationType.LIVING_ROOM_REMODEL, projectId);
        // Mock Room entity as service returns entity, not DTO
        Room mockRoom = Room.builder()
                .exposedId(roomId)
                .name("Living Room")
                .renovationType(RenovationType.LIVING_ROOM_REMODEL)
                .phases(new ArrayList<>()) // Initialize phases list
                .totalCost(0) // Initialize totalCost
                .build();

        when(roomService.createRoom(any(RoomRequestDTO.class))).thenReturn(mockRoom);

        mockMvc.perform(post("/rooms") // Changed endpoint to /rooms as per controller
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()) // Changed to isOk as createRoom returns RoomResponseDTO directly
                .andExpect(jsonPath("$.exposedId").value(roomId.toString()))
                .andExpect(jsonPath("$.name").value("Living Room"))
                .andExpect(jsonPath("$.renovationType").value("LIVING_ROOM_REMODEL"));

        verify(roomService, times(1)).createRoom(any(RoomRequestDTO.class));
    }

    @Test
    void createRoom_ProjectNotFound() throws Exception {
        RoomRequestDTO request = new RoomRequestDTO("Kitchen", RenovationType.KITCHEN_RENOVATION, projectId);

        when(roomService.createRoom(any(RoomRequestDTO.class)))
                .thenThrow(new ElementNotFoundException("Project not found"));

        mockMvc.perform(post("/rooms") // Changed endpoint to /rooms as per controller
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getRoomById_Success() throws Exception {
        // Mock Room entity
        Room mockRoom = Room.builder()
                .exposedId(roomId)
                .name("Bedroom")
                .renovationType(RenovationType.BEDROOM_RENOVATION)
                .phases(new ArrayList<>())
                .totalCost(0)
                .build();

        when(roomService.getRoomById(roomId)).thenReturn(mockRoom);

        mockMvc.perform(get("/rooms/{exposedId}", roomId)) // Changed path variable name
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exposedId").value(roomId.toString()))
                .andExpect(jsonPath("$.name").value("Bedroom"))
                .andExpect(jsonPath("$.renovationType").value("BEDROOM_RENOVATION"));
    }

    @Test
    void getRoomById_NotFound() throws Exception {
        when(roomService.getRoomById(roomId))
                .thenThrow(new ElementNotFoundException("Room not found"));

        mockMvc.perform(get("/rooms/{exposedId}", roomId)) // Changed path variable name
                .andExpect(status().isNotFound());
    }

    @Test
    void getRoomsByProject_Success() throws Exception {
        Room room1 = Room.builder().exposedId(UUID.randomUUID()).name("Living Room").renovationType(RenovationType.LIVING_ROOM_REMODEL).phases(new ArrayList<>()).totalCost(0).build();
        Room room2 = Room.builder().exposedId(UUID.randomUUID()).name("Kitchen").renovationType(RenovationType.KITCHEN_RENOVATION).phases(new ArrayList<>()).totalCost(0).build();
        List<Room> rooms = Arrays.asList(room1, room2); // Service returns Room entities

        when(roomService.getRoomsByProject(projectId)).thenReturn(rooms);

        mockMvc.perform(get("/rooms/project/{projectExposedId}", projectId)) // Corrected endpoint
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Living Room"))
                .andExpect(jsonPath("$[1].name").value("Kitchen"));
    }

    @Test
    void getRoomsByProject_Empty() throws Exception {
        when(roomService.getRoomsByProject(projectId)).thenReturn(List.of());

        mockMvc.perform(get("/rooms/project/{projectExposedId}", projectId)) // Corrected endpoint
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void updateRoom_Success() throws Exception {
        RoomRequestDTO request = new RoomRequestDTO("Updated Room", RenovationType.FULL_HOME_RENOVATION, null); // projectExposedId not needed for update
        Room mockRoom = Room.builder()
                .exposedId(roomId)
                .name("Updated Room")
                .renovationType(RenovationType.FULL_HOME_RENOVATION)
                .phases(new ArrayList<>())
                .totalCost(0)
                .build();

        when(roomService.updateRoom(any(UUID.class), any(RoomRequestDTO.class))).thenReturn(mockRoom);

        mockMvc.perform(put("/rooms/{exposedId}", roomId) // Changed path variable name
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Room"))
                .andExpect(jsonPath("$.renovationType").value("FULL_HOME_RENOVATION"));
    }

    @Test
    void updateRoom_NotFound() throws Exception {
        RoomRequestDTO request = new RoomRequestDTO("Bathroom", RenovationType.BATHROOM_RENOVATION, null);

        when(roomService.updateRoom(any(UUID.class), any(RoomRequestDTO.class)))
                .thenThrow(new ElementNotFoundException("Room not found"));

        mockMvc.perform(put("/rooms/{exposedId}", roomId) // Changed path variable name
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteRoom_Success() throws Exception {
        doNothing().when(roomService).deleteRoom(roomId);

        mockMvc.perform(delete("/rooms/{exposedId}", roomId)) // Changed path variable name
                .andExpect(status().isOk()); // Changed to isOk as deleteRoom returns void, and controller returns void

        verify(roomService, times(1)).deleteRoom(roomId);
    }

    @Test
    void deleteRoom_NotFound() throws Exception {
        doThrow(new ElementNotFoundException("Room not found"))
                .when(roomService).deleteRoom(roomId);

        mockMvc.perform(delete("/rooms/{exposedId}", roomId)) // Changed path variable name
                .andExpect(status().isNotFound());
    }
}
