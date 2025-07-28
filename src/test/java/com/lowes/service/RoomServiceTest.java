package com.lowes.service;

import com.lowes.dto.request.RoomRequestDTO;
import com.lowes.entity.Project;
import com.lowes.entity.Room;
import com.lowes.entity.enums.RenovationType;
import com.lowes.exception.ElementNotFoundException;
import com.lowes.repository.ProjectRepository;
import com.lowes.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoomServiceTest {

    @InjectMocks
    private RoomService roomService;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private ProjectRepository projectRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateRoom_Success() {
        UUID projectId = UUID.randomUUID();

        RoomRequestDTO request = new RoomRequestDTO();
        request.setName("Living Room");
        request.setRenovationType(RenovationType.LIVING_ROOM_REMODEL);
        request.setProjectExposedId(projectId);

        Project project = Project.builder().exposedId(projectId).build();
        Room room = Room.builder().name("Living Room").renovationType(RenovationType.LIVING_ROOM_REMODEL).project(project).build();

        when(projectRepository.findByExposedId(projectId)).thenReturn(Optional.of(project));
        when(roomRepository.save(any(Room.class))).thenReturn(room);

        Room savedRoom = roomService.createRoom(request);

        assertNotNull(savedRoom);
        assertEquals("Living Room", savedRoom.getName());
        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @Test
    void testCreateRoom_ProjectNotFound() {
        UUID projectId = UUID.randomUUID();

        RoomRequestDTO request = new RoomRequestDTO();
        request.setName("Kitchen");
        request.setRenovationType(RenovationType.KITCHEN_RENOVATION);
        request.setProjectExposedId(projectId);

        when(projectRepository.findByExposedId(projectId)).thenReturn(Optional.empty());

        assertThrows(ElementNotFoundException.class, () -> roomService.createRoom(request));
    }

    @Test
    void testUpdateRoom_Success() {
        UUID roomId = UUID.randomUUID();
        Room existingRoom = Room.builder().exposedId(roomId).name("Old").renovationType(RenovationType.BEDROOM_RENOVATION).build();

        RoomRequestDTO request = new RoomRequestDTO();
        request.setName("Updated Room");
        request.setRenovationType(RenovationType.FULL_HOME_RENOVATION);

        when(roomRepository.findByExposedId(roomId)).thenReturn(Optional.of(existingRoom));
        when(roomRepository.save(any(Room.class))).thenReturn(existingRoom);

        Room updatedRoom = roomService.updateRoom(roomId, request);

        assertEquals("Updated Room", updatedRoom.getName());
        assertEquals(RenovationType.FULL_HOME_RENOVATION, updatedRoom.getRenovationType());
    }

    @Test
    void testUpdateRoom_NotFound() {
        UUID roomId = UUID.randomUUID();
        RoomRequestDTO request = new RoomRequestDTO();

        when(roomRepository.findByExposedId(roomId)).thenReturn(Optional.empty());

        assertThrows(ElementNotFoundException.class, () -> roomService.updateRoom(roomId, request));
    }

    @Test
    void testGetRoomById_Success() {
        UUID roomId = UUID.randomUUID();
        Room room = Room.builder().exposedId(roomId).name("Bathroom").build();

        when(roomRepository.findByExposedId(roomId)).thenReturn(Optional.of(room));

        Room fetched = roomService.getRoomById(roomId);

        assertNotNull(fetched);
        assertEquals("Bathroom", fetched.getName());
    }

    @Test
    void testGetRoomById_NotFound() {
        UUID roomId = UUID.randomUUID();

        when(roomRepository.findByExposedId(roomId)).thenReturn(Optional.empty());

        assertThrows(ElementNotFoundException.class, () -> roomService.getRoomById(roomId));
    }

    @Test
    void testGetRoomsByProject() {
        UUID projectId = UUID.randomUUID();
        List<Room> roomList = List.of(
                Room.builder().name("Balcony").renovationType(RenovationType.BALCONY_RENOVATION).build()
        );

        when(roomRepository.findByProjectExposedId(projectId)).thenReturn(roomList);

        List<Room> rooms = roomService.getRoomsByProject(projectId);

        assertEquals(1, rooms.size());
        assertEquals("Balcony", rooms.get(0).getName());
    }

    @Test
    void testDeleteRoom_Success() {
        UUID roomId = UUID.randomUUID();
        Room room = Room.builder().exposedId(roomId).build();

        when(roomRepository.findByExposedId(roomId)).thenReturn(Optional.of(room));

        roomService.deleteRoom(roomId);

        verify(roomRepository, times(1)).delete(room);
    }

    @Test
    void testDeleteRoom_NotFound() {
        UUID roomId = UUID.randomUUID();

        when(roomRepository.findByExposedId(roomId)).thenReturn(Optional.empty());

        assertThrows(ElementNotFoundException.class, () -> roomService.deleteRoom(roomId));
    }
}