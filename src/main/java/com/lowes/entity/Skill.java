package com.lowes.entity;

import com.lowes.entity.enums.SkillType;
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
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private SkillType name; // e.g., "Plumbing", "Electrical", "Painting"

    private Double basePrice;

    @ManyToMany(mappedBy = "skills")
    private List<Vendor> vendors;


    @OneToMany(mappedBy = "requiredSkill")
    private List<Phase> phases;

}