package com.lowes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "skills")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Skill {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    private BigDecimal base_price;

    @Column(unique = true, nullable = false , name="name")
    private String name; // e.g., "Plumbing", "Electrical", "Painting"

    @ManyToMany(mappedBy = "skills")
    private List<Vendor> vendors;

    @OneToMany(mappedBy = "requiredSkill")
    private List<Phase> phases;


}
