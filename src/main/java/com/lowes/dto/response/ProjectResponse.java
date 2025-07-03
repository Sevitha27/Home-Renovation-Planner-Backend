package com.lowes.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lowes.entity.enums.ServiceType;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

 public record ProjectResponse(
        // @JsonProperty("exposeId")
    UUID id, 
    String name,
    ServiceType serviceType,
     Double estimatedBudget,
  LocalDate startDate,
  LocalDate endDate, 
  UUID ownerId,
     List<RoomResponse> rooms,
          Double totalProjectCost // New field
          ){}


