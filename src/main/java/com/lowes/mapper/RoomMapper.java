package com.lowes.mapper;

import com.lowes.dto.response.RoomResponseDTO;
import com.lowes.dto.response.PhaseResponseDTO;
import com.lowes.entity.Room;

public class RoomMapper {

    public static RoomResponseDTO toDTO(Room room) {
        return new RoomResponseDTO(
                room.getExposedId(),
                room.getName(),
                room.getRenovationType(),
                room.getPhases()
                        .stream()
                        .map(PhaseResponseDTO::toDTO)
                        .toList(),
                room.getTotalCost()
        );
    }
}