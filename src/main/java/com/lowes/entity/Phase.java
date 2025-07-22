package com.lowes.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Table(name = "phase")
@Entity
public class Phase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

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

    @Column(nullable = false)
    private String phaseName;
    private String description;

    @Column(nullable = false)
    private LocalDate startDate;
    @Column(nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private PhaseType phaseType;

    private Integer totalPhaseCost = 0;
    private Integer vendorCost;
    private Integer totalPhaseMaterialCost = 0;

    @OneToMany(mappedBy = "phase",fetch = FetchType.EAGER)
    @JsonIgnore
    @OrderBy("id ASC")
    private List<PhaseMaterial> phaseMaterialList = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PhaseStatus phaseStatus;



}
