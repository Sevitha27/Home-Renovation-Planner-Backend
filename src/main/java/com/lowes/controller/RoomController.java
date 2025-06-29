package com.lowes.controller;

import com.lowes.DTO.RoomRequestDTO;
import com.lowes.entity.Room;
import com.lowes.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping("/add")
    public Room addRoom(@RequestBody RoomRequestDTO dto) {
        return roomService.addRoomToProject(dto);
    }
}
