package com.lowes.dto.response;

import com.lowes.entity.enums.RenovationType;
import java.util.List;
import java.util.UUID;

public record RoomResponse(
        UUID exposedId,  // Changed from id
        String name,
        RenovationType renovationType,
        List<PhaseResponseDTO> phases,
        Integer totalCost
) {}