package com.lowes.entity;

// import java.util.List;
import java.util.UUID;

import com.lowes.entity.enums.RenovationType;

// import jakarta.persistence.CascadeType;
import jakarta.persistence.*;
// import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    
    @Id
    @GeneratedValue(generator = "UUID")
    // @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;
    
    private String name;


   // @Column(unique = true, updatable = false)
    //private String exposedId; // Frontend-facing

    //@PrePersist
    //private void generateExposedId() {
      //  if(exposedId == null) {
        //    exposedId = "PROJ-" + UUID.randomUUID().toString().substring(0,8);
        //}
    //}
    
    @Enumerated(EnumType.STRING)
    private RenovationType renovationType;
    
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
      private Integer totalRoomCost;  // New field to store sum of phase costs
}
