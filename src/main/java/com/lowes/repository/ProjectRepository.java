package com.lowes.repository;

import com.lowes.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
    List<Project> findByOwnerId(Long ownerId);

    @Query("SELECT p FROM Project p WHERE p.exposedId = :exposedId")
    Optional<Project> findByExposedId(@Param("exposedId") UUID exposedId);

    @Query("SELECT p FROM Project p JOIN p.owner o WHERE p.exposedId = :exposedId AND o.id = :userId")
    Optional<Project> findByExposedIdAndOwnerId(
            @Param("exposedId") UUID exposedId,
            @Param("userId") Long userId
    );
    boolean existsByExposedIdAndOwnerExposedId(UUID projectExposedId, UUID ownerExposedId);
}