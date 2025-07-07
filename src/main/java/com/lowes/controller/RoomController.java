package com.lowes.controller;

import com.lowes.dto.request.RoomRequestDTO;
import com.lowes.dto.response.PhaseResponseDTO;
import com.lowes.dto.response.RoomResponse;
import com.lowes.entity.Room;
import com.lowes.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public Room createRoom(@RequestBody RoomRequestDTO dto) {
        return roomService.createRoom(dto);
    }

    @PutMapping("/{id}")
    public Room updateRoom(@PathVariable UUID id, @RequestBody RoomRequestDTO dto) {
        return roomService.updateRoom(id, dto);
    }

    @GetMapping("/{id}")
    public RoomResponse getRoom(@PathVariable UUID id) {
        Room room = roomService.getRoomById(id);

        List<PhaseResponseDTO> phaseDTOs = room.getPhases().stream()
                .map(PhaseResponseDTO::new)
                .toList();

        return new RoomResponse(
                room.getExposedId(),
                room.getName(),
                room.getRenovationType(),
                phaseDTOs,
                room.getTotalCost()
        );
    }


    @GetMapping("/project/{projectId}")
    public List<Room> getProjectRooms(@PathVariable UUID projectId) {
        return roomService.getRoomsByProject(projectId);
    }

    @DeleteMapping("/{id}")
    public void deleteRoom(@PathVariable UUID id) {
        roomService.deleteRoom(id);
    }


}