package com.lowes.entity;


import com.lowes.entity.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "users")
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID exposedId;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String contact;

    @Column(name = "url_image")
    private String pic;

    @PrePersist
    public void prePersist(){
        if (exposedId == null) {
            exposedId = UUID.randomUUID();
        }
        if(pic == null || pic.isEmpty()){
            pic = "https://res.cloudinary.com/dpuk8nzcl/image/upload/v1751453296/profile-pic-holder_gbqh7h.jpg";
        }
    }

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // One-to-Many: A user owns many projects
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Project> projects;

    @ManyToMany(mappedBy = "customers")
    private List<Vendor> vendorsServingThisUser;

    // One-to-Many: A user can write many reviews
    @OneToMany(mappedBy = "reviewer", cascade = CascadeType.ALL)
    private List<VendorReview> vendorReviews;

}
