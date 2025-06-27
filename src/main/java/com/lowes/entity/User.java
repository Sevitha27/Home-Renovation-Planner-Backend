package com.lowes.entity;

import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "app_user")  // <--- change table name
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}
