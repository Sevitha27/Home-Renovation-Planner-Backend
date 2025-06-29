package com.lowes.service;
import com.lowes.DTO.ProjectRequestDTO;
import com.lowes.DTO.ProjectResponseDTO;
import com.lowes.dto.response.BudgetOverviewResponseDTO;
import com.lowes.entity.*;

import com.lowes.mapper.ProjectMapper;
import com.lowes.repository.ProjectRepository;
import com.lowes.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectResponseDTO createProject(ProjectRequestDTO dto) {
        User owner = userRepository.findById(dto.getOwnerId()).orElseThrow();
        Project project = ProjectMapper.toEntity(dto, owner);
        Project saved = projectRepository.save(project);
        return ProjectMapper.toDTO(saved);
    }

    public BudgetOverviewResponseDTO getBudgetOverview(UUID projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        double estimatedBudget = project.getEstimatedBudget();
        int totalVendorCost = 0;
        int totalMaterialCost = 0;

        for (Room room : project.getRooms()) {
            for (Phase phase : room.getPhases()) {
                if (phase.getVendorCost() != null) {
                    totalVendorCost += phase.getVendorCost();
                }

                for (PhaseMaterial pm : phase.getPhaseMaterialList()) {
                    totalMaterialCost += pm.getPricePerQuantity() * pm.getQuantity();
                }
            }
        }

        int totalActualCost = totalVendorCost + totalMaterialCost;
        double percentageSpent = (estimatedBudget == 0) ? 0 : (totalActualCost * 100.0 / estimatedBudget);
        double budgetDifference = estimatedBudget - totalActualCost;
        boolean isOverBudget = totalActualCost > estimatedBudget;
        String budgetStatusColor = isOverBudget ? "RED" : "GREEN";

        return BudgetOverviewResponseDTO.builder()
                .estimatedBudget(estimatedBudget)
                .totalVendorCost(totalVendorCost)
                .totalMaterialCost(totalMaterialCost)
                .totalActualCost(totalActualCost)
                .percentageSpent(Math.round(percentageSpent * 100.0) / 100.0)
                .remainingBudget(budgetDifference)
                .isOverBudget(isOverBudget)
                .budgetStatusColor(budgetStatusColor)
                .build();



    }
}