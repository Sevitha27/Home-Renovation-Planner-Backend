package com.lowes.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.lowes.entity.enums.PhaseType;
import com.lowes.entity.enums.Unit;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Entity
@FieldDefaults(level=AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhaseMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(nullable = false, unique = true)
    UUID exposedId;

    @Column(nullable = false)
    String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    Unit unit;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    PhaseType phaseType;

    @Column(nullable = false)
    int pricePerQuantity;

    @Column(nullable = false)
    int quantity;

    @Column(nullable = false)
    int totalPrice;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(nullable = false)
    Phase phase;

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(nullable = false)
    Material material;
}
