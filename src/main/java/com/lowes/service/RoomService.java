package com.lowes.service;

import com.lowes.dto.request.RoomRequestDTO;
import com.lowes.entity.Project;
import com.lowes.entity.Room;
import com.lowes.exception.ElementNotFoundException;
// import com.lowes.exception.SecurityException;
import com.lowes.repository.ProjectRepository;
import com.lowes.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class RoomService {

    @Autowired private RoomRepository roomRepository;
    @Autowired private ProjectRepository projectRepository;

    @PreAuthorize("@projectSecurity.isProjectOwner(#dto.projectExposedId, authentication.principal.id)")
    public Room createRoom(RoomRequestDTO dto) {
        Project project = projectRepository.findByExposedId(dto.getProjectExposedId())
                .orElseThrow(() -> new ElementNotFoundException("Project not found"));
        
        Room room = Room.builder()
                .name(dto.getName())
                .renovationType(dto.getRenovationType())
                .project(project)
                .build();
        
        return roomRepository.save(room);
    }

    @PreAuthorize("@roomSecurity.isRoomOwner(#exposedId, authentication.principal.id)")
    public Room updateRoom(UUID exposedId, RoomRequestDTO dto) {
        Room room = roomRepository.findByExposedId(exposedId)
                .orElseThrow(() -> new ElementNotFoundException("Room not found"));
        
        room.setName(dto.getName());
        room.setRenovationType(dto.getRenovationType());
        
        return roomRepository.save(room);
    }

    @PreAuthorize("@roomSecurity.isRoomOwner(#exposedId, authentication.principal.id)")
    public Room getRoomById(UUID exposedId) {
        return roomRepository.findByExposedId(exposedId)
                .orElseThrow(() -> new ElementNotFoundException("Room not found"));

                

    }

    @PreAuthorize("@projectSecurity.isProjectOwner(#projectExposedId, authentication.principal.id)")
    public List<Room> getRoomsByProject(UUID projectExposedId) {
        return roomRepository.findByProjectExposedId(projectExposedId);
    }

    @PreAuthorize("@roomSecurity.isRoomOwner(#exposedId, authentication.principal.id)")
    public void deleteRoom(UUID exposedId) {
        Room room = roomRepository.findByExposedId(exposedId)
                .orElseThrow(() -> new ElementNotFoundException("Room not found"));
        roomRepository.delete(room);
    }
}