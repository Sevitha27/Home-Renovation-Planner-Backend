package com.lowes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
// import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Project {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

private String name;
private Double estimate; // New field
    private LocalDate startDate; // New field
    private LocalDate endDate;   //new field
     @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id" ,nullable = false)
    private User owner;
    
// Change rooms mapping to eager fetch
@OneToMany(mappedBy = "project", 
           cascade = CascadeType.ALL, 
           orphanRemoval = true,
           fetch = FetchType.LAZY) // Keep lazy but ensure proper loading in queries
private List<Room> rooms = new ArrayList<>();
private Double totalProjectCost;  // New field to store sum of room costs
 

}
