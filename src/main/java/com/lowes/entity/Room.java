package com.lowes.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.lowes.enums.RenovationType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Enumerated(EnumType.STRING)
    RenovationType renovationType;

    @ManyToOne
    @JoinColumn(name = "project_id")
    Project project;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    @JsonManagedReference("room-phase")
    List<Phase> phases;
}
