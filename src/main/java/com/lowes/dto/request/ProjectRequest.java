package com.lowes.dto.request;

import java.time.LocalDate;
import java.util.List;

import com.lowes.dto.response.RoomResponse;
import com.lowes.entity.enums.ServiceType;

public record ProjectRequest(
      String name,
      ServiceType serviceType,
     Double estimatedBudget,
     LocalDate startDate,
    LocalDate endDate,
       List<RoomResponse> rooms
){}
