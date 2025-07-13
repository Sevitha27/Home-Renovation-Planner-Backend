package com.lowes.entity;

import com.lowes.entity.enums.RenovationType;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Internal DB ID

    @Column(nullable = false, unique = true, updatable = false)
    private UUID exposedId; // Public-facing ID

    private String name;

    @Enumerated(EnumType.STRING)
    private RenovationType renovationType;

    private Integer totalCost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Phase> phases = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (exposedId == null) {
            exposedId = UUID.randomUUID();
        }
    }

    @PreUpdate
    public void calculateTotalCost() {
        if (phases == null) {
            this.totalCost = 0;
            return;
        }
        
        this.totalCost = phases.stream()
            .mapToInt(phase -> 
                (phase.getTotalPhaseMaterialCost() != null ? phase.getTotalPhaseMaterialCost() : 0) +
                (phase.getVendorCost() != null ? phase.getVendorCost() : 0)
            )
            .sum();
    }

     // Add this accessor for query method
    public Project getProject() {
        return project;
    }
}