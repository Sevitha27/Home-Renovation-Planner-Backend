package com.lowes.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.lowes.entity.enums.RenovationType;
import com.lowes.entity.enums.Unit;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

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
    int phaseMaterialId;

//    @Column(nullable = false, updatable = false, unique = true)
//    UUID exposedId = UUID.randomUUID();

    @Column(nullable = false)
    String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    Unit unit;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    RenovationType renovationType;

    @Column(nullable = false)
    double pricePerQuantity;

    @Column(nullable = false)
    int quantity;

    @Column(nullable = false)
    double totalPrice;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(nullable = false)
    Phase phase;

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(nullable = false)
    Material material;
}
