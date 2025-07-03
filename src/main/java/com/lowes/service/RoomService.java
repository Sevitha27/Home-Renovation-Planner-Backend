package com.lowes.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.lowes.Exception.AccessDeniedException;
import com.lowes.Exception.NotFoundException;
import com.lowes.dto.request.RoomRequest;
import com.lowes.dto.response.RoomResponse;
import com.lowes.entity.Project;
import com.lowes.entity.Room;
import com.lowes.entity.User;
import com.lowes.mapper.RoomMapper;
import com.lowes.repository.ProjectRepository;
import com.lowes.repository.RoomRepository;
import com.lowes.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepo;
    private final ProjectRepository projectRepo;
    private final UserRepository userRepo;
        private final PhaseService phaseService;


    public RoomResponse createRoom(RoomRequest request, UUID userId) {
        // Verify user exists
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found"));
        
        // Get project with ownership check
        Project project = projectRepo.findById(request.projectId())
            .orElseThrow(() -> new NotFoundException("Project not found"));
            
        if (!project.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("You don't own this project");
        }
        
        // Create and save room
        Room room = new Room();
        room.setName(request.name());
        room.setRenovationType(request.renovationType());
        room.setProject(project);
        
        Room savedRoom = roomRepo.save(room);
        return RoomMapper.toDTO(savedRoom);
    }

    public List<RoomResponse> getRoomsByProject(UUID projectId, UUID userId) {
        // Verify project ownership first
        if (!projectRepo.existsByIdAndOwnerId(projectId, userId)) {
            throw new AccessDeniedException("Access denied");
        }
        
        return roomRepo.findByProjectId(projectId).stream()
            .map(RoomMapper::toDTO)
            .toList();
    }


// Calculate total cost for a single room  
public RoomResponse calculateRoomCost(UUID roomId, UUID userId) {
    // Step 1: Get room from DB
    Room room = roomRepo.findById(roomId)
        .orElseThrow(() -> new NotFoundException("Room not found"));
    
    // Step 2: Verify ownership (Critical Isolation Check!)
    if (!room.getProject().getOwner().getId().equals(userId)) {
        throw new AccessDeniedException("You don't own this room's project!");
    }
    
    // Step 3: Calculate cost (sum of all phases)
    // 
    int totalCost = phaseService.getPhasesByRoomId(roomId).stream()
            .mapToInt(phase -> phase.getTotalPhaseCost() != null ? phase.getTotalPhaseCost() : 0)
            .sum();
    
    // Step 4: Update room cost
    room.setTotalRoomCost(totalCost);
    roomRepo.save(room);
    
    return new RoomResponse(
            room.getId(),
            room.getName(),
            room.getRenovationType(),totalCost
    );
}
    // Get all rooms with costs for a project
    public List<RoomResponse> getRoomsWithCosts(UUID projectId, UUID userId) {
        List<Room> rooms = roomRepo.findByProjectId(projectId);
        return rooms.stream()
                .map(room -> new RoomResponse(
                    room.getId(),
                    room.getName(),
                    room.getRenovationType(),
                    room.getTotalRoomCost()  // Updated RoomResponse needed
                ))
                .toList();
    }


}