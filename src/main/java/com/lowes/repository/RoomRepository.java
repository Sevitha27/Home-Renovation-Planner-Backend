package com.lowes.repository;

import com.lowes.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomRepository extends JpaRepository<Room, Long> {
    @Query("SELECT r FROM Room r WHERE r.project.exposedId = :projectExposedId")
    List<Room> findByProjectExposedId(@Param("projectExposedId") UUID projectExposedId);
    
    @Query("SELECT r FROM Room r WHERE r.exposedId = :exposedId")
    Optional<Room> findByExposedId(@Param("exposedId") UUID exposedId);
    
    @Query("SELECT r FROM Room r JOIN r.project p JOIN p.owner o " +
           "WHERE r.exposedId = :exposedId AND o.id = :userId")
    Optional<Room> findByExposedIdAndOwnerId(
        @Param("exposedId") UUID exposedId,
        @Param("userId") Long userId
    );
}