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
        Project project = projectRepo.findByExposedId(projectId)
                .orElseThrow(() -> new com.lowes.exception.NotFoundException("Project not found"));

        if (!project.getOwner().getExposedId().equals(userId)) {
            throw new com.lowes.exception.AccessDeniedException("You don't own this project!");
        }

        double estimatedBudget = project.getEstimatedBudget() != null ? project.getEstimatedBudget() : 0;
//      double totalProjectCost = project.getTotalCost() != null ? project.getTotalCost() : 0;

        // 2. Prepare room-wise cost split
       List<Room> rooms = roomRepo.findByProjectExposedId(projectId);
        double totalProjectCost = project.getTotalCost() ;
//
        List<RoomCostDTO> roomDTOs = rooms.stream()
                .peek(Room::calculateTotalCost)
                .map(room -> new RoomCostDTO(
                        room.getId(),
                        room.getName(),
                        room.getTotalCost() != null ? room.getTotalCost() : 0
                ))
                .collect(Collectors.toList());


        // 3. Prepare phase-wise split with vendor & material cost
//        List<Phase> phases = phaseRepo.findAllByRoom_Id(projectId);
        List<PhaseCostDTO> phaseDTOs = new ArrayList<>();

//        for (Phase phase : phases) {
//            int vendorCost = phase.getVendorCost() != null ? phase.getVendorCost() : 0;
//            int materialCost = phase.getTotalPhaseMaterialCost() != null ? phase.getTotalPhaseMaterialCost() : 0;
//            int totalPhaseCost = phase.getTotalPhaseCost() != null ? phase.getTotalPhaseCost() : vendorCost + materialCost;
//
//            phaseDTOs.add(new PhaseCostDTO(
//                    phase.getId(),
//                    phase.getPhaseName(),
//                    phase.getPhaseType().toString(),
//                    vendorCost,
//                    materialCost,
//                    totalPhaseCost
//            ));
//        }
        for (Room room : rooms) {
            List<Phase> phases = phaseRepo.findAllByRoom_Id(room.getId());

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
        }

        return new BudgetOverviewResponseDTO(
                projectId,
                project.getName(),
                estimatedBudget,
//                totalProjectCost,
                project.getTotalCost(),
                roomDTOs,
                phaseDTOs
        );
    }
}

