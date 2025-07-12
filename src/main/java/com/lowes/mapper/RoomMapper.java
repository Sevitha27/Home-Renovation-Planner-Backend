package com.lowes.mapper;

import com.lowes.dto.response.RoomResponse;
import com.lowes.entity.Room;

public class RoomMapper {

    public static RoomResponse toDTO(Room room) {
        return new RoomResponse(
            room.getExposedId(),
            room.getName(),
            room.getRenovationType(),
            room.getPhases().stream().map(PhaseMapper::toDTO).toList(),
            room.getTotalCost()
        );
    }
}