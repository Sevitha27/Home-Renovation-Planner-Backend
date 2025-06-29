package com.lowes.repository;

import com.lowes.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    // You can define custom queries here if needed
}
