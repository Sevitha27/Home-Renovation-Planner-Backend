package com.lowes.controller;

import com.lowes.dto.request.RoomRequestDTO;
import com.lowes.dto.response.RoomResponseDTO;
import com.lowes.mapper.RoomMapper;
import com.lowes.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER') and " +
            "@projectSecurity.isProjectOwner(#dto.projectExposedId, authentication.principal.exposedId)")
    public RoomResponseDTO createRoom(
            @RequestBody RoomRequestDTO dto,
            Authentication authentication
    ) {
        return RoomMapper.toDTO(roomService.createRoom(dto));
    }

    @PutMapping("/{exposedId}")
    @PreAuthorize("hasRole('CUSTOMER') and " +
            "@roomSecurity.isRoomOwner(#exposedId, authentication.principal.exposedId)")
    public RoomResponseDTO updateRoom(
            @PathVariable UUID exposedId,
            @RequestBody RoomRequestDTO dto
    ) {
        return RoomMapper.toDTO(roomService.updateRoom(exposedId, dto));
    }

    @GetMapping("/{exposedId}")
    public RoomResponseDTO getRoom(@PathVariable UUID exposedId) {
        return RoomMapper.toDTO(roomService.findByExposedId(exposedId));
    }

    @GetMapping("/project/{projectExposedId}")
    @PreAuthorize("hasRole('CUSTOMER') and " +
            "@projectSecurity.isProjectOwner(#projectExposedId, authentication.principal.exposedId)")
    public List<RoomResponseDTO> getProjectRooms(@PathVariable UUID projectExposedId) {
        return roomService.getRoomsByProject(projectExposedId).stream()
                .map(RoomMapper::toDTO)
                .toList();
    }

    @DeleteMapping("/{exposedId}")
    @PreAuthorize("hasRole('CUSTOMER') and " +
            "@roomSecurity.isRoomOwner(#exposedId, authentication.principal.exposedId)")
    public void deleteRoom(@PathVariable UUID exposedId) {
        roomService.deleteRoom(exposedId);
    }
}

