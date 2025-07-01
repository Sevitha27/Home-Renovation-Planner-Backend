package com.lowes.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.lowes.entity.enums.PhaseStatus;
import com.lowes.entity.enums.PhaseType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Table(name = "phase")
@Entity
public class Phase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID Id;

    @ManyToOne
    @JoinColumn(name = "project_id")
    @JsonBackReference("project-phase")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "skill_id")
    private Skill requiredSkill;

    @ManyToOne
    @JoinColumn(name="room_id")
    @JsonBackReference("room-phase")
    private Room room;

    @ManyToOne
    @JoinColumn(name="vendor_id")
    @JsonBackReference("vendor-phase")
    private Vendor vendor;

    private String phaseName;
    private String description;

    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private PhaseType phaseType;

    private Integer totalPhaseCost = 0;
    private Integer totalPhaseMaterialCost = 0;

    private Integer vendorCost;

    @OneToMany(mappedBy = "phase",fetch = FetchType.EAGER)
    @JsonManagedReference("phase-material")
    @OrderBy("id ASC")
    private List<PhaseMaterial> phaseMaterialList = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PhaseStatus phaseStatus=PhaseStatus.NOTSTARTED;



}
