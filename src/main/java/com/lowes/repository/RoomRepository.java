package com.lowes.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.lowes.entity.Room;

import java.util.List;
import java.util.UUID;

public interface RoomRepository extends JpaRepository<Room, UUID> {
    List<Room> findByProjectId(UUID projectId);
}
