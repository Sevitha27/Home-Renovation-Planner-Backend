package com.lowes.mapper;

import com.lowes.dto.response.PhaseResponseDTO;
import com.lowes.dto.response.RoomResponse;
import com.lowes.entity.Room;

public class RoomMapper {

    public static RoomResponse toDTO(Room room) {
        return new RoomResponse(
                
            room.getExposedId(),  // Use exposedId instead of id
                room.getName(),
                room.getRenovationType(),
                room.getPhases().stream().map(PhaseResponseDTO::toDTO).toList(),
                room.getTotalCost()  // Changed from getTotalRoomCost()
        );
    }
}