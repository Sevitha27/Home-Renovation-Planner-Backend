package com.lowes.service;

import com.lowes.dto.RoomRequestDTO;
import com.lowes.entity.Project;
import com.lowes.entity.Room;
import com.lowes.repository.ProjectRepository;
import com.lowes.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final ProjectRepository projectRepository;

    public Room addRoomToProject(RoomRequestDTO dto) {
        Project project = projectRepository.findById(dto.getProjectId()).orElseThrow();

        Room room = new Room();
        room.setRenovationType(dto.getRenovationType());
        room.setProject(project);

        return roomRepository.save(room);
    }
}
