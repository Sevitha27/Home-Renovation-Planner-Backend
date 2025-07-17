package com.lowes.security;

import com.lowes.entity.Room;
import com.lowes.repository.RoomRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoomSecurity {

    private final RoomRepository roomRepository;
    private final ProjectSecurity projectSecurity;

    public boolean isRoomOwner(UUID roomExposedId, UUID userExposedId) {
        return roomRepository.existsByExposedIdAndProjectOwnerExposedId(
                roomExposedId,
                userExposedId
        );
    }
}