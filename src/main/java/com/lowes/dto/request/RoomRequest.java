package com.lowes.dto.request;

import java.util.UUID;

import com.lowes.entity.enums.RenovationType;

public record RoomRequest(
    String name, 
    RenovationType renovationType,
    UUID projectId // Needed to link room to project
) {}
