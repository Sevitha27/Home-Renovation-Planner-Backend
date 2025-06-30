package com.lowes.controller;


import com.lowes.entity.enums.PhaseStatus;
import com.lowes.entity.enums.PhaseType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/enums")
@RequiredArgsConstructor
public class EnumController {

    @GetMapping("/phase-types")
    public PhaseType[] getPhaseTypes() {
        return PhaseType.values();
    }

    @GetMapping("/phase-statuses")
    public PhaseStatus[] getPhaseStatuses() {
        return PhaseStatus.values();
    }
}
