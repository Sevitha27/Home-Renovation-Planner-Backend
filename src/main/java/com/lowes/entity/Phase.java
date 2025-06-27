package com.lowes.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public class Phase {

    @Id
    int id;

    @JsonBackReference
    @OneToMany(mappedBy = "phase",fetch = FetchType.EAGER)
    List<PhaseMaterial> phaseMaterialList;
}
