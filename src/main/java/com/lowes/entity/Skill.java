package com.lowes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    private Number base_price;

    @Column(unique = true, nullable = false)
    private String name; // e.g., "Plumbing", "Electrical", "Painting"

    @ManyToMany(mappedBy = "skills")
    private List<Vendor> vendors;

    @OneToMany(mappedBy = "requiredSkill")
    private List<Phase> phases;

}
