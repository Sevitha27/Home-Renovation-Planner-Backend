package com.lowes.service;

import com.lowes.dto.response.BudgetOverviewResponseDTO;
import com.lowes.dto.response.PhaseCostDTO;
import com.lowes.dto.response.RoomCostDTO;
import com.lowes.entity.Phase;
import com.lowes.entity.Project;
import com.lowes.entity.Room;
import com.lowes.repository.PhaseRepository;
import com.lowes.repository.ProjectRepository;
import com.lowes.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BudgetOverviewService {
    private final ProjectRepository projectRepo;
    private final RoomRepository roomRepo;
    private final PhaseRepository phaseRepo;

    public BudgetOverviewResponseDTO getBudgetOverview(UUID projectId, UUID userId) {
        // 1. Validate project access
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new com.lowes.Exception.NotFoundException("Project not found"));

        if (!project.getOwner().getId().equals(userId)) {
            throw new com.lowes.Exception.AccessDeniedException("You don't own this project!");
        }

        double estimatedBudget = project.getEstimatedBudget() != null ? project.getEstimatedBudget() : 0;
        double totalProjectCost = project.getTotalProjectCost() != null ? project.getTotalProjectCost() : 0;

        // 2. Prepare room-wise cost split
        List<Room> rooms = roomRepo.findByProjectId(projectId);
        List<RoomCostDTO> roomDTOs = rooms.stream()
                .map(room -> new RoomCostDTO(
                        room.getId(),
                        room.getName(),
                        room.getTotalRoomCost() != null ? room.getTotalRoomCost() : 0
                ))
                .collect(Collectors.toList());

        // 3. Prepare phase-wise split with vendor & material cost
        List<Phase> phases = phaseRepo.findAllByProject_Id(projectId);
        List<PhaseCostDTO> phaseDTOs = new ArrayList<>();

        for (Phase phase : phases) {
            int vendorCost = phase.getVendorCost() != null ? phase.getVendorCost() : 0;
            int materialCost = phase.getTotalPhaseMaterialCost() != null ? phase.getTotalPhaseMaterialCost() : 0;
            int totalPhaseCost = phase.getTotalPhaseCost() != null ? phase.getTotalPhaseCost() : vendorCost + materialCost;

            phaseDTOs.add(new PhaseCostDTO(
                    phase.getId(),
                    phase.getPhaseName(),
                    phase.getPhaseType().toString(),
                    vendorCost,
                    materialCost,
                    totalPhaseCost
            ));
        }

        return new BudgetOverviewResponseDTO(
                projectId,
                project.getName(),
                estimatedBudget,
                totalProjectCost,
                roomDTOs,
                phaseDTOs
        );
    }
}
