package com.lowes.repository;

import com.lowes.entity.Project;
import com.lowes.entity.User;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
     List<Project> findByOwnerId(UUID ownerId); // Must match exactly
 @EntityGraph(attributePaths = "owner")
    Optional<Project> findWithOwnerById(UUID id);
    
    boolean existsByIdAndOwnerId(UUID id, UUID ownerId);
    
    @Query("SELECT p FROM Project p JOIN FETCH p.owner WHERE p.id = :id")
    Optional<Project> findByIdWithOwner(@Param("id") UUID id);
    List<Project> findByOwner(User owner);
}
