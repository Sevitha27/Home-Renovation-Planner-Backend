package com.lowes.entity;

import com.lowes.enums.ServiceType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@FieldDefaults(level= AccessLevel.PRIVATE)
public class Project {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false)
    private UUID id;
    private String name;

    @Enumerated(EnumType.STRING)
    ServiceType serviceType;

    Double estimatedBudget;
    LocalDate startDate;
    LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Room> rooms;

}


