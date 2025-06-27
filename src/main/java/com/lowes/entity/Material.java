package com.lowes.entity;


import com.lowes.entity.enums.PhaseType;
import com.lowes.entity.enums.Unit;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

//    @Column(nullable = false, updatable = false, unique = true)
//    UUID exposedId = UUID.randomUUID();

    @Column(nullable = false,unique = true)
    String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    Unit unit;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    PhaseType phaseType;

    @Column(nullable = false)
    int pricePerQuantity;

    @JsonBackReference
    @OneToMany(mappedBy = "material", fetch = FetchType.EAGER)
    List<PhaseMaterial> phaseMaterialList = new ArrayList<>();

    @Column(nullable = false)
    boolean deleted = false;
}
