package com.lowes.controller;

import com.lowes.dto.request.RoomRequestDTO;
import com.lowes.dto.response.RoomResponse;
import com.lowes.entity.Room;
import com.lowes.mapper.RoomMapper;
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
    public RoomResponse createRoom(@RequestBody RoomRequestDTO dto) {
        return RoomMapper.toDTO(roomService.createRoom(dto));
    }

    @PutMapping("/{exposedId}")
    public RoomResponse updateRoom(
            @PathVariable UUID exposedId,
            @RequestBody RoomRequestDTO dto
    ) {
        return RoomMapper.toDTO(roomService.updateRoom(exposedId, dto));
    }

    @GetMapping("/{exposedId}")
    public RoomResponse getRoom(@PathVariable UUID exposedId) {
        return RoomMapper.toDTO(roomService.getRoomById(exposedId));
    }

    @GetMapping("/project/{projectExposedId}")
    public List<RoomResponse> getProjectRooms(@PathVariable UUID projectExposedId) {
        return roomService.getRoomsByProject(projectExposedId).stream()
                .map(RoomMapper::toDTO)
                .toList();
    }

    @DeleteMapping("/{exposedId}")
    public void deleteRoom(@PathVariable UUID exposedId) {
        roomService.deleteRoom(exposedId);
    }
}