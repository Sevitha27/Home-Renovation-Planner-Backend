package com.lowes.dto.response;

import java.util.List;
import java.util.UUID;

import com.lowes.entity.enums.RenovationType;

public record RoomResponse(
    UUID id,
    String name,
    RenovationType renovationType,
    // List<PhaseResponse> phases,
            Integer totalCost 

) {}
