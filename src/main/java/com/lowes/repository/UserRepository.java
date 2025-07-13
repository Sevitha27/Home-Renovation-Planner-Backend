package com.lowes.repository;

import com.lowes.entity.User;
import com.lowes.entity.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    User findByExposedId(UUID exposedId);
    Page<User> findByRole(Role role, Pageable pageable);
}
