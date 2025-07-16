package com.lowes.entity;

import com.lowes.entity.enums.ServiceType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @Column(nullable = false, unique = true, updatable = false)
    private UUID exposedId; // Public-facing ID

    private String name;

    @Enumerated(EnumType.STRING)
    private ServiceType serviceType;

    private LocalDate startDate;
    private LocalDate endDate;
    private Integer estimatedBudget;

   @Transient
    public Integer getTotalCost() {
        return rooms.stream()
               .mapToInt(room -> room.getTotalCost() != null ? room.getTotalCost() : 0)
               .sum();
    }

    private Integer TotalCost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Room> rooms = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (exposedId == null) {
            exposedId = UUID.randomUUID();
        }
    }


}