package com.lowes.DTO;

import com.lowes.enums.RenovationType;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomRequestDTO {
    private RenovationType renovationType;
    private UUID projectId;
}
