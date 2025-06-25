package com.lowes.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Table(name = "phaseMaterial")
@Entity
public class PhaseMaterial {

    @ManyToOne
    @JoinColumn(name="phase_id")
    @JsonBackReference("phase-material")
    private Phase phase;

    private Integer cost;

    @Id
    private Long id;

}
