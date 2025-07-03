package com.lowes.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Vendor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID exposedId;

    @PrePersist
    public void prePersist() {
        if (exposedId == null) {
            exposedId = UUID.randomUUID();
        }
    }
    private String companyName;

    private String experience;

    private Boolean available;

    private Boolean approved;

    @ManyToMany
    @JoinTable(
            name = "vendor_skills",
            joinColumns = @JoinColumn(name = "vendor_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private List<Skill> skills;


    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL)
    private List<VendorReview> reviews;



    @ManyToMany
    @JoinTable(
            name = "vendor_customers",
            joinColumns = @JoinColumn(name = "vendor_id"),
            inverseJoinColumns = @JoinColumn(name = "customer_id")
    )
    private List<User> customers;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id") // this is the vendor's user account
    private User user;

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL)
    @JsonManagedReference("vendor-phase")
    private List<Phase> assignedPhases;

    public boolean isAvailable() {
        return available;
    }

}
