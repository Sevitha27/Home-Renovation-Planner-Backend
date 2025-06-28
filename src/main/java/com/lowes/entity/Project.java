package com.lowes.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@FieldDefaults(level= AccessLevel.PRIVATE)
public class Project {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    @JsonBackReference("user-project")
    private User user;

    Double estimatedBudget;
    LocalDate startDate;
    LocalDate endDate;
    private String name;

    @Enumerated(EnumType.STRING)
    ServiceType serviceType;

    @OneToMany(mappedBy = "project",cascade = CascadeType.ALL)
    @JsonManagedReference("project-phase")
    private List<Phase> phasesList;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Room> rooms;






}
