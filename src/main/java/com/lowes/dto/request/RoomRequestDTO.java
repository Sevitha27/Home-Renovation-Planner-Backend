package com.lowes.dto.request;

import com.lowes.entity.enums.RenovationType;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomRequestDTO {
    private String name;
    private RenovationType renovationType;
    private UUID projectId;
}