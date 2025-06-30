package com.lowes.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.lowes.entity.enums.RenovationType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    String name;

    @Enumerated(EnumType.STRING)
    RenovationType renovationType;

    @ManyToOne
    @JoinColumn(name = "project_id")
    Project project;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    @JsonManagedReference("room-phase")
    List<Phase> phases;
}