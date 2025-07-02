package com.lowes.dto.request;

import java.time.LocalDate;
import java.util.List;

import com.lowes.dto.response.RoomResponse;

public record ProjectRequest(
      String name,
     Double estimate,
     LocalDate startDate,
    LocalDate endDate,
       List<RoomResponse> rooms
){}
