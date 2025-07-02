package com.lowes.dto.response;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

 public record ProjectResponse(
    UUID id, 
    String name,
     Double estimate, 
  LocalDate startDate,
  LocalDate endDate, 
  UUID ownerId,
     List<RoomResponse> rooms,
          Double totalProjectCost // New field
          ){}


