package com.lowes.security;

import com.lowes.repository.RoomRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoomSecurity {

    private final RoomRepository roomRepository;

    public boolean isRoomOwner(UUID roomExposedId, UUID userExposedId) {
        return roomRepository.existsByExposedIdAndProjectOwnerExposedId(
            roomExposedId, 
            userExposedId
        );
    }
}