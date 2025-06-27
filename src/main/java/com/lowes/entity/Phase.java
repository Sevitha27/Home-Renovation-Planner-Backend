package com.lowes.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.lowes.entity.enums.PhaseStatus;
import com.lowes.entity.enums.PhaseType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

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
    @JoinColumn(name="vendor_id")
    @JsonBackReference("vendor-phase")
    private Vendor vendor;

    private String phaseName;
    private String description;

    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private PhaseType phaseType;

    private Integer totalPhaseCost;
    private Integer vendorCost;

    @OneToMany(mappedBy = "phase", cascade = CascadeType.ALL)
    @JsonManagedReference("phase-material")
    private List<PhaseMaterial> phaseMaterialList;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PhaseStatus phaseStatus=PhaseStatus.NOTSTARTED;



}
