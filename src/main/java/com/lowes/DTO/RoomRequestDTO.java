package com.lowes.DTO;

import com.lowes.enums.RenovationType;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomRequestDTO {
    private RenovationType renovationType;
    private Long projectId;
}
