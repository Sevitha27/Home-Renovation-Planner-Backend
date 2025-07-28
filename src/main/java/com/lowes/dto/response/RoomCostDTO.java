package com.lowes.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class RoomCostDTO {
    private UUID roomId;
    private String roomName;
    private double totalRoomCost;
}
