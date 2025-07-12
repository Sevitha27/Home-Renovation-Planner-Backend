package com.lowes.dto.response;

import com.lowes.entity.enums.RenovationType;
import java.util.List;
import java.util.UUID;

public record RoomResponse(
        UUID exposedId,
        String name,
        RenovationType renovationType,
        List<PhaseResponse> phases,
        Integer totalCost
) {}