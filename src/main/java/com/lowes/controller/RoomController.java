package com.lowes.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lowes.Exception.AccessDeniedException;
import com.lowes.Exception.NotFoundException;
// import com.lowes.config.CurrentUser;
import com.lowes.dto.request.RoomRequest;
import com.lowes.dto.response.RoomResponse;
import com.lowes.entity.Room;
// import com.lowes.entity.User;
import com.lowes.service.JwtService;
import com.lowes.service.RoomService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;
    private final JwtService jwtService;


    @PostMapping
    public ResponseEntity<?> createRoom(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody RoomRequest request) {
        
        try {
            // Extract and validate JWT
            String token = authHeader.substring(7);
            if (!jwtService.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            UUID userId = jwtService.extractUserId(token);
            RoomResponse response = roomService.createRoom(request, userId);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid request");
        }
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<?> getRoomsByProject(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID projectId) {
        
        try {
            String token = authHeader.substring(7);
            UUID userId = jwtService.extractUserId(token);
            
            List<RoomResponse> rooms = roomService.getRoomsByProject(projectId, userId);
            return ResponseEntity.ok(rooms);
            
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    //  room cost + project cost



//    phase + phase  ko ak per room cost
    @GetMapping("/{roomId}/cost")
    public ResponseEntity<RoomResponse> getRoomWithCost(
        @PathVariable UUID roomId,
        @RequestHeader("Authorization") String token) {
        
        UUID userId = jwtService.extractUserId(token.substring(7));
        return ResponseEntity.ok(roomService.calculateRoomCost(roomId, userId));
    }

    //   complete room 
    @GetMapping("/project/{projectId}/costs")
    public ResponseEntity<List<RoomResponse>> getRoomCostsForProject(
        @PathVariable UUID projectId,
        @RequestHeader("Authorization") String token) {
        
        UUID userId = jwtService.extractUserId(token.substring(7));
        return ResponseEntity.ok(roomService.getRoomsWithCosts(projectId, userId));
    }
}