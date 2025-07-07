package com.lowes.service;

import com.lowes.dto.request.RoomRequestDTO;
import com.lowes.entity.Project;
import com.lowes.entity.Room;
import com.lowes.exception.ElementNotFoundException;
import com.lowes.repository.ProjectRepository;
import com.lowes.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RoomService {

    @Autowired private RoomRepository roomRepository;
    @Autowired private ProjectRepository projectRepository;

    // Changed to long for user ID
    @PreAuthorize("@projectSecurity.isProjectOwner(#dto.projectId, authentication.principal.id)")
    public Room createRoom(RoomRequestDTO dto) {
        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new ElementNotFoundException("Project not found"));
        
        Room room = Room.builder()
                .name(dto.getName())
                .renovationType(dto.getRenovationType())
                .project(project)
                .build();
        
        return roomRepository.save(room);
    }

    // Changed to long for user ID
    @PreAuthorize("@roomSecurity.isRoomOwner(#id, authentication.principal.id)")
    public Room updateRoom(UUID id, RoomRequestDTO dto) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ElementNotFoundException("Room not found"));
        
        room.setName(dto.getName());
        room.setRenovationType(dto.getRenovationType());
        
        return roomRepository.save(room);
    }

    // Changed to long for user ID

    public Room getRoomById(UUID id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ElementNotFoundException("Room not found"));
    }

    // Changed to long for user ID
    @PreAuthorize("@projectSecurity.isProjectOwner(#projectId, authentication.principal.id)")
    public List<Room> getRoomsByProject(UUID projectId) {
        return roomRepository.findByProjectId(projectId);
    }

    // Changed to long for user ID
    @PreAuthorize("@roomSecurity.isRoomOwner(#id, authentication.principal.id)")
    public void deleteRoom(UUID id) {
        roomRepository.deleteById(id);
    }
}