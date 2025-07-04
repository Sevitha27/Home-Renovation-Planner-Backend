package com.lowes.controller;

import com.lowes.dto.response.BudgetOverviewResponseDTO;
import com.lowes.entity.User;
import com.lowes.service.BudgetOverviewService;
import com.lowes.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController

@RequestMapping("/api/projects")
@PreAuthorize("hasRole('ROLE_CUSTOMER')")
public class BudgetOverviewController {

    @Autowired
    private JwtService jwtService;
    @Autowired
    private BudgetOverviewService budgetOverviewService;

    @GetMapping("/{projectId}/budget-overview")
    public ResponseEntity<BudgetOverviewResponseDTO> getBudgetOverview(@PathVariable UUID projectId,@RequestHeader("Authorization") String token
    ) {
        //User user = (User) SecurityContext.getContext().getAuthentication().getPrincipal()
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //UUID userId = jwtService.extractUserId(token.substring(7));
        UUID userId=user.getExposedId();
        BudgetOverviewResponseDTO response = budgetOverviewService.getBudgetOverview(projectId, userId);
        return ResponseEntity.ok(response);
    }
}